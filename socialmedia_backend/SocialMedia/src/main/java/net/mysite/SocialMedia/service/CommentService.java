package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.domain.Comment;
import net.mysite.SocialMedia.domain.Posts;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.dto.CommentDto;
import net.mysite.SocialMedia.err.MissingFieldException;
import net.mysite.SocialMedia.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public Comment createComment(Posts post, User user, CommentDto commentDto) {

        String cmtString = commentDto.getText();
        Long cmtId = commentDto.getId();
        if(cmtString.isEmpty()){ throw new MissingFieldException("Comment cannot be empty."); }
        if(cmtId == null){ throw new MissingFieldException("Comment id cannot be empty."); }

        Comment newComment = new Comment();
        newComment.setId(cmtId);
        newComment.setPost(post);
        newComment.setUser(user);
        newComment.setText(cmtString);
        newComment.setCreatedOn(LocalDateTime.now());

        return commentRepository.save(newComment);
    }

    public Set<Comment> getAllCommentsOfPost(Posts post) {
        Set<Comment> cmtSet = commentRepository.findCommentsByPost(post);
        if(cmtSet.isEmpty()) { return Collections.emptySet(); }
        return cmtSet;
    }
}
