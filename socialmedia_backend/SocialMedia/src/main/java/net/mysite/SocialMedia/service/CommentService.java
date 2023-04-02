package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.domain.Comment;
import net.mysite.SocialMedia.domain.Posts;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.dto.CommentDto;
import net.mysite.SocialMedia.err.MissingFieldException;
import java.util.Set;

public interface CommentService {


    Comment createComment(Posts post, User user, CommentDto commentDto) throws MissingFieldException;

    Set<Comment> getAllCommentsOfPost(Posts post);
}
