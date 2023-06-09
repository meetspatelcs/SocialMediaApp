package net.mysite.SocialMedia.repository;

import net.mysite.SocialMedia.domain.Friend;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.dto.FriendIdDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Set;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query("SELECT u FROM Friend u WHERE (u.user = :user OR u.requestedUser = :user)")
    Set<Friend> findAllFriends(User user);

    @Query("SELECT u FROM Friend u WHERE (u.user = :user OR u.requestedUser = :user) AND u.status = :myStatus")
    Set<Friend> findAllFriendsWithStatus(User user, String myStatus);

    @Query("SELECT u.requestedUser FROM Friend u WHERE (u.user.id = :user AND u.status = :myStatus)")
    Set<User> findAllSent(Long user, String myStatus);
    @Query("SELECT u.user FROM Friend u WHERE (u.requestedUser.id = :user AND u.status = :myStatus)")
    Set<User> findAllReceived(Long user, String myStatus);

    @Query("SELECT u.requestedUser FROM Friend u WHERE (u.user.id = :userId)")
    Set<User> findSentAll(Long userId);
    @Query("SELECT u.user FROM Friend u WHERE (u.requestedUser.id = :userId)")
    Set<User> findReceivedAll(Long userId);
    @Query("SELECT u FROM Friend u WHERE (u.user = :user AND u.requestedUser.id = :friendId) OR (u.user.id = :friendId AND u.requestedUser = :user)")
    Friend findByUserAndFriend(User user, Long friendId);


    @Query("SELECT new net.mysite.SocialMedia.dto.FriendIdDto(u.requestedUser.id, u.status) FROM Friend u WHERE (u.user.id = :userId)")
    Set<FriendIdDto> findFriendIdAllSent( Long userId);
    @Query("SELECT new net.mysite.SocialMedia.dto.FriendIdDto( u.user.id, u.status) FROM Friend u WHERE (u.requestedUser.id = :userId)")
    Set<FriendIdDto> findFriendIdAllReceived( Long userId);

    @Query("SELECT new net.mysite.SocialMedia.dto.FriendIdDto(u.requestedUser.id, u.status) FROM Friend u WHERE (u.user.id = :id AND u.requestedUser.id = :friendId)")
    FriendIdDto findSentByIds(Long friendId, Long id);
    @Query("SELECT new net.mysite.SocialMedia.dto.FriendIdDto(u.user.id, u.status) FROM Friend u WHERE (u.requestedUser.id = :id AND u.user.id = :friendId)")
    FriendIdDto findReceivedByIds(Long friendId, Long id);

    @Query("SELECT u.requestedUser.id FROM Friend u WHERE (u.user.id = :user AND u.status = :friendStat)")
    Set<Long> findLongAllSent(Long user, String friendStat);

    @Query("SELECT u.user.id FROM Friend u WHERE (u.requestedUser.id = :user AND u.status = :friendStat)")
    Set<Long> findLongAllReceived(Long user, String friendStat);


//    @Query("SELECT DISTINCT u2.user FROM Friend u1 JOIN u1.requestedUser u2 WHERE u1.user = :friend AND u2 != :user AND u2 NOT IN (SELECT u3.requestedUser FROM Friend u3 WHERE u3.user = :user AND u3.status = :friendStat) AND u1.status = :friendStat")
//    Set<User> findNotFriendByFriendFromSent(User friend, User user, String friendStat);
}
