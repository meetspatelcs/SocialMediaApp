package net.mysite.SocialMedia.company.sevice;

import net.mysite.SocialMedia.company.domain.PageComments;
import net.mysite.SocialMedia.company.domain.PagePost;
import net.mysite.SocialMedia.company.dto.PageCommentDto;
import net.mysite.SocialMedia.company.repository.PageCommentRepository;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.err.MissingFieldException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

@Service
public class PageCommentService {

    @Autowired
    private PageCommentRepository pageCommentRepository;

    public PageComments save(PagePost pagePost, User user, PageCommentDto pageCommentDto) {
        String desc = pageCommentDto.getText();
        if(desc.isEmpty()){ throw new MissingFieldException("Text for comment cannot be empty."); }

        PageComments newPageComments = new PageComments();
        newPageComments.setCreatedOn(LocalDateTime.now());
        newPageComments.setPagePost(pagePost);
        newPageComments.setUser(user);
        newPageComments.setText(desc);
        return pageCommentRepository.save(newPageComments);
    }

    public Set<PageComments> getAllCommentsOfPagePost(PagePost pagePost) {
        Set<PageComments> commentsSet = pageCommentRepository.findAllCommentsByPagePost(pagePost);
        if(commentsSet.isEmpty()) { return Collections.emptySet(); }
        return commentsSet;
    }
}
