package net.mysite.SocialMedia.repository;

import net.mysite.SocialMedia.domain.Comment;
import net.mysite.SocialMedia.domain.Posts;
import net.mysite.SocialMedia.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE (c.post = :post)")
    Set<Comment> findCommentsByPost(Posts post);
}
