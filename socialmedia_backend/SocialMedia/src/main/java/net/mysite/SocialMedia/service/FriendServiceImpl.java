package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.Enums.FriendRequestStatusEnum;
import net.mysite.SocialMedia.domain.Friend;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.dto.FriendIdDto;
import net.mysite.SocialMedia.err.*;
import net.mysite.SocialMedia.repository.FriendRepository;
import net.mysite.SocialMedia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static net.mysite.SocialMedia.company.constants.FriendConstants.Approved_Friend_stat;

@Service
public class FriendServiceImpl implements FriendService{

    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InfoService infoService;

    @Override
    public Set<Friend> findById(User user) throws EmptyFriendsException, UserNotFoundException{
        Set<Friend> friendsSet = friendRepository.findAllFriends(user);
        if(friendsSet == null) { throw new EmptyFriendsException("Not friends with any users yet."); }
        return friendsSet;
    }

    @Override
    public Set<User> getFriendList(Long userId) throws UserNotFoundException, EmptyFriendsException{
        if(!userRepository.existsById(userId)){ throw new UserNotFoundException("User does not exist."); }

        Set<User> friendList = new HashSet<>();
        Set<User> sentRequest = friendRepository.findAllSent(userId, Approved_Friend_stat);
        Set<User> receivedRequest = friendRepository.findAllReceived(userId, Approved_Friend_stat);

        if(!sentRequest.isEmpty()){ friendList.addAll(sentRequest); }
        if(!receivedRequest.isEmpty()){ friendList.addAll(receivedRequest); }
        if(friendList.isEmpty()) { throw new EmptyFriendsException("Could not find any friends."); }
        return friendList;
    }

    @Override
    public Optional<Friend> findById(Long friendId){ return friendRepository.findById(friendId); }

    @Override
    public Friend save(Friend friend) throws MissingFieldException{
        Long friendId = friend.getId();
        User user = friend.getUser();
        User user2 = friend.getRequestedUser();
        String friendStat = friend.getStatus();

        if(friendId == null){ throw new MissingFieldException("Id cannot be null."); }
        if(user == null) { throw new MissingFieldException("User cannot be null."); }
        if(user2 == null) { throw new MissingFieldException("User cannot be null."); }
        if(friendStat == null) { throw new MissingFieldException("Status cannot be null."); }

        return friendRepository.save(friend);
    }

    @Override
    public Friend save(Long friendId, User user) throws UserNotFoundException, InvalidFriendRequestException, FriendRequestAlreadyExistsException{
        User friendUser = userRepository.findById(friendId)
                .orElseThrow(() -> new UserNotFoundException("User does not exist."));
        if(friendUser.equals(user)){ throw new InvalidFriendRequestException("Cannot send friend request to yourself."); }

        Optional<Friend> optionalFriend = getFriendFromEntity(user, friendId);
        if(optionalFriend.isPresent()){ throw new FriendRequestAlreadyExistsException("Cannot send friend request. A Friend has already been sent or received."); }

        Friend newFriend  = new Friend();
        newFriend.setRequestedUser(friendUser);
        newFriend.setUser(user);
        newFriend.setStatus(FriendRequestStatusEnum.PENDING_REQUEST.getStatus());
        return friendRepository.save(newFriend);
    }

    @Override
    public void deleteById(Long friendId) { friendRepository.deleteById(friendId); }

    @Override
    public Set<FriendIdDto> getFriendListAll(Long userId) throws UserNotFoundException, EmptyFriendsException {
        if(!userRepository.existsById(userId)){ throw new UserNotFoundException("User does not exist."); }

        Set<FriendIdDto> friends = new HashSet<>();
        Set<FriendIdDto> sentFriendIds = friendRepository.findFriendIdAllSent(userId);
        Set<FriendIdDto> receivedFriendIds = friendRepository.findFriendIdAllReceived(userId);

        if(!sentFriendIds.isEmpty()){ friends.addAll(sentFriendIds); }
        if(!receivedFriendIds.isEmpty()){ friends.addAll(receivedFriendIds); }
        if(friends.isEmpty()){ throw new EmptyFriendsException("Could not find any friends."); }

        return friends;
    }

    @Override
    public Friend getByUserAndFriend(Long friendId, User user) throws NotFriendException {
        Optional<Friend> optionalFriend = getFriendFromEntity(user, friendId);
        Friend friend = optionalFriend.orElseThrow(() -> new NotFriendException("Friend not found"));
        return friend;
    }

    @Override
    public FriendIdDto getStatByIds(Long friendId, Long id) {
        Optional<FriendIdDto> sentStat = Optional.ofNullable(friendRepository.findSentByIds(friendId, id));
        if(sentStat.isPresent()){ return sentStat.get(); }

        Optional<FriendIdDto> receivedStat = Optional.ofNullable(friendRepository.findReceivedByIds(friendId, id));
        if(receivedStat.isPresent()){ return receivedStat.get(); }
        return new FriendIdDto(friendId, "Null");
    }

    @Override
    public void deleteFriendByUserAndFriend(Long friendId, User user) throws NotFriendException{
        Optional<Friend> optionalFriend = getFriendFromEntity(user, friendId);

        optionalFriend.ifPresentOrElse(
                friend -> deleteById(friend.getId()),
                () -> {throw new NotFriendException("Failed to remove friend.");});
    }

    private Optional<Friend> getFriendFromEntity(User user, Long friendId){
        return Optional.ofNullable(friendRepository.findByUserAndFriend(user, friendId));
    }
}
