package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.domain.Comment;
import net.mysite.SocialMedia.domain.Posts;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.dto.CommentDto;
import net.mysite.SocialMedia.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Set;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public Comment createComment(Posts post, User user, CommentDto commentDto) {
        try{
            Comment newComment = new Comment();
            newComment.setId(commentDto.getId());
            newComment.setPost(post);
            newComment.setUser(user);
            newComment.setText(commentDto.getText());
            newComment.setCreatedOn(LocalDateTime.now());

            return commentRepository.save(newComment);
        }
        catch (Exception e){
            throw new RuntimeException("An error occurred. Failed to create comment.");
        }
    }

    public Set<Comment> getAllCommentsOfPost(Posts post) {
        return commentRepository.findCommentsByPost(post);
    }
}
