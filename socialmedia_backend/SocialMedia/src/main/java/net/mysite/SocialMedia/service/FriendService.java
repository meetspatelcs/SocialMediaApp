package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.domain.Friend;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.dto.FriendIdDto;
import net.mysite.SocialMedia.err.*;

import java.util.*;

public interface FriendService {

     Set<Friend> findById(User user) throws EmptyFriendsException, UserNotFoundException;

     Set<User> getFriendList(Long userId) throws UserNotFoundException, EmptyFriendsException;

     Optional<Friend> findById(Long friendId);

     Friend save(Friend friend) throws MissingFieldException;

     Friend save(Long friendId, User user) throws UserNotFoundException, InvalidFriendRequestException, FriendRequestAlreadyExistsException;

     void deleteById(Long friendId);

     Set<FriendIdDto> getFriendListAll(Long userId) throws UserNotFoundException, EmptyFriendsException;

     Friend getByUserAndFriend(Long friendId, User user) throws NotFriendException;

     FriendIdDto getStatByIds(Long friendId, Long id);

     void deleteFriendByUserAndFriend(Long friendId, User user) throws NotFriendException;
}