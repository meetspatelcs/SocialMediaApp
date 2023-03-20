package net.mysite.SocialMedia.repository;

import net.mysite.SocialMedia.domain.Posts;
import net.mysite.SocialMedia.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;


public interface PostRepository extends JpaRepository<Posts, Long> {
    @Query("SELECT u FROM Posts u WHERE u.user = :user ORDER BY u.id DESC")
    List<Posts> findByUser(User user);

    @Query("SELECT u FROM Posts u WHERE u.user.id = :userId ORDER BY u.id DESC")
    List<Posts> getByuserId(Long userId);
    @Query("SELECT u FROM Posts u WHERE (u.user = :currUser AND u.creationDate >= :endPoint) ORDER BY u.id DESC")
    List<Posts> findPostWithTimeByUser(User currUser, LocalDateTime endPoint);

//    @Query("SELECT p.id From Posts p WHERE p.user = :user ORDER BY p.id DESC")
//    List<Long> findIdByUser(User user);
}
