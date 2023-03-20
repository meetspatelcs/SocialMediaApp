package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.Enums.FriendRequestStatusEnum;
import net.mysite.SocialMedia.domain.Friend;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.dto.FriendIdDto;
import net.mysite.SocialMedia.errors.*;
import net.mysite.SocialMedia.repository.FriendRepository;
import net.mysite.SocialMedia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class FriendService {

    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private UserRepository userRepository;

    public Set<Friend> findById(User user){
        return friendRepository.findAllFriends(user);
    }
    public Set<User> getFriendList(Long userId){
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()){
            Set<User> friendList = new HashSet<>();
            Set<User> sentRequest = friendRepository.findAllSent(userId, "Friends");
            Set<User> receivedRequest = friendRepository.findAllReceived(userId, "Friends");

            if(!sentRequest.isEmpty()){
                friendList.addAll(sentRequest);
            }
            if(!receivedRequest.isEmpty()){
                friendList.addAll(receivedRequest);
            }

            return friendList;
        }
        else{
            throw new UserNotFoundException("User with ID " + userId + " not found.");
        }
    }

    public Optional<Friend> findById(Long friendId){
        return friendRepository.findById(friendId);
    }

    public Friend save(Friend friend){
        return friendRepository.save(friend);
    }

    public Friend save(Long friendId, User user){
        User friendUser = userRepository.findById(friendId).orElseThrow(() -> new UserNotFoundException("User with ID " + friendId + " not found."));

        if(friendUser.equals(user)){
            throw new FriendRequestException("You cannot add yourself as a friend");
        }

        Optional<Friend> isFriend = Optional.ofNullable(friendRepository.findByUserAndFriend(user, friendId));
        if(isFriend.isPresent()){
            throw new FriendRequestException("You or the user already have sent a request");
        }

        Friend newFriend  = new Friend();
        newFriend.setRequestedUser(friendUser);
        newFriend.setUser(user);
        newFriend.setStatus(FriendRequestStatusEnum.PENDING_REQUEST.getStatus());
        return friendRepository.save(newFriend);
    }
    public void deleteById(Long friendId) {
         friendRepository.deleteById(friendId);
    }
    public Set<FriendIdDto> getFriendListAll(Long userId) {

        if(!userRepository.existsById(userId)){
            throw new UserNotFoundException("User not found with ID " + userId);
        }

        Set<FriendIdDto> friends = new HashSet<>();
        Set<FriendIdDto> sentFriendIds = friendRepository.findFriendIdAllSent(userId);
        Set<FriendIdDto> receivedFriendIds = friendRepository.findFriendIdAllReceived(userId);

        if(!sentFriendIds.isEmpty()){
            friends.addAll(sentFriendIds);
        }
        if(!receivedFriendIds.isEmpty()){
            friends.addAll(receivedFriendIds);
        }
        if(friends.isEmpty()){
            throw new FriendListNotFoundException("No friends found for user with ID " + userId);
        }
        return friends;
    }

    public Friend getByUserAndFriend(Long friendId, User user) {
        Friend isFriend = friendRepository.findByUserAndFriend(user, friendId);
        if(isFriend == null){
            throw new FriendNotFoundException("Friend with ID " + friendId + " not found.");
        }
        return isFriend;
    }

    public FriendIdDto getStatByIds(Long friendId, Long id) {
        Optional<FriendIdDto> sentStat = Optional.ofNullable(friendRepository.findSentByIds(friendId, id));
        if(sentStat.isPresent()){
            return sentStat.get();
        }

        Optional<FriendIdDto> receivedStat = Optional.ofNullable(friendRepository.findReceivedByIds(friendId, id));
        if(receivedStat.isPresent()){
            return receivedStat.get();
        }
        return new FriendIdDto(friendId, "Null");
    }

    public void deleteFriendByUserAndFriend(Long friendId, User user) {
        Friend isFriend = friendRepository.findByUserAndFriend(user, friendId);
        if(isFriend == null){
            throw new NotFoundException("Friend was not found.");
        }
        try{
            friendRepository.deleteById(isFriend.getId());
        }
        catch (DataAccessException e){
            throw new DatabaseException("Failed to remove friend.");
        }
    }
}
