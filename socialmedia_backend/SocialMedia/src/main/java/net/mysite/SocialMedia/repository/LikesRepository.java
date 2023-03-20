package net.mysite.SocialMedia.repository;

import net.mysite.SocialMedia.domain.Likes;
import net.mysite.SocialMedia.domain.Posts;
import net.mysite.SocialMedia.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    @Query("SELECT l FROM Likes l WHERE (l.user = :user AND l.post = :post)")
    Likes findLikeByPostAndUser(Posts post, User user);
    @Modifying
    @Transactional
    @Query("DELETE FROM Likes l WHERE (l.user = :user AND l.post.id = :postId)")
    void deleteLikeByPostAndUser(Long postId, User user);
}
