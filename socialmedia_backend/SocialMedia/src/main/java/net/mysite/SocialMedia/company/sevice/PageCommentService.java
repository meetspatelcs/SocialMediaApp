package net.mysite.SocialMedia.company.sevice;

import net.mysite.SocialMedia.company.domain.PageComments;
import net.mysite.SocialMedia.company.domain.PagePost;
import net.mysite.SocialMedia.company.dto.PageCommentDto;
import net.mysite.SocialMedia.company.repository.PageCommentRepository;
import net.mysite.SocialMedia.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class PageCommentService {

    @Autowired
    private PageCommentRepository pageCommentRepository;

    public PageComments save(PagePost pagePost, User user, PageCommentDto pageCommentDto) {
        PageComments newPageComments = new PageComments();
        newPageComments.setCreatedOn(LocalDateTime.now());
        newPageComments.setPagePost(pagePost);
        newPageComments.setUser(user);
        newPageComments.setText(pageCommentDto.getText());

        return pageCommentRepository.save(newPageComments);
    }

    public Set<PageComments> getAllCommentsOfPagePost(PagePost pagePost) {
        return pageCommentRepository.findAllCommentsByPagePost(pagePost);
    }
}
