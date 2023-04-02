package net.mysite.SocialMedia.company.sevice;

import net.mysite.SocialMedia.company.domain.PageComments;
import net.mysite.SocialMedia.company.domain.PagePost;
import net.mysite.SocialMedia.company.dto.PageCommentDto;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.err.MissingFieldException;

import java.util.Set;


public interface PageCommentService {

     PageComments save(PagePost pagePost, User user, PageCommentDto pageCommentDto) throws MissingFieldException;

     Set<PageComments> getAllCommentsOfPagePost(PagePost pagePost);
}
