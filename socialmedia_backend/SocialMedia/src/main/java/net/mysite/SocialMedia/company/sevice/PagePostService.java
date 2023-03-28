package net.mysite.SocialMedia.company.sevice;

import net.mysite.SocialMedia.company.domain.Page;
import net.mysite.SocialMedia.company.domain.PagePost;
import net.mysite.SocialMedia.company.domain.PagePostPhoto;
import net.mysite.SocialMedia.company.repository.PagePostPhotoRepository;
import net.mysite.SocialMedia.company.repository.PagePostRepository;
import net.mysite.SocialMedia.err.MissingFieldException;
import net.mysite.SocialMedia.err.PagePostNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

@Service
public class PagePostService {

    @Autowired
    private PagePostRepository pagePostRepository;
    @Autowired
    private PageService pageService;
    @Autowired
    private PagePostPhotoRepository pagePostPhotoRepository;

    public PagePost getById(Long pagePostId){
        return pagePostRepository.findById(pagePostId)
                .orElseThrow(() -> new PagePostNotFoundException("Page Post was not found."));
    }

    public PagePost save(Long pageId, String description) {
        if(pageId == null || description.isEmpty()){ throw new MissingFieldException("Required filed is empty."); }

        PagePost newPagePost = new PagePost();
        newPagePost.setDescription(description);
        newPagePost.setCreatedOn(LocalDateTime.now());
        Page myPage = pageService.getById(pageId);

        newPagePost.setPage(myPage);
        return pagePostRepository.save(newPagePost);
    }

    public void removeById(Long pageId, Long postId) {
        pagePostPhotoRepository.deleteById(postId);
        Page page = pageService.getById(pageId);
        pagePostRepository.deleteByIds(page, postId);
    }

    public PagePost UpdatePagePostById(Long postId, String description) {
        PagePost pagePost = getById(postId);
        pagePost.setDescription(description);
        pagePost.setCreatedOn(LocalDateTime.now());
        return pagePostRepository.save(pagePost);
    }

    public Set<PagePost> getAllByPage(Page myPage) {
        Set<PagePost> allPagePosts = pagePostRepository.findAllByPage(myPage);
        if(allPagePosts.isEmpty()){ return Collections.emptySet(); }
        return allPagePosts;
    }

    public Set<PagePostPhoto> getMediaInfoList(Page page, String mediaType) {
        Set<PagePostPhoto> tempPostMedia = pagePostPhotoRepository.findAllByPage(page);
        if(tempPostMedia.isEmpty()){ throw new PagePostNotFoundException("Post for page does not exists."); }
        tempPostMedia.removeIf(elem -> !elem.getFileType().split("/")[0].equals(mediaType));
        return tempPostMedia;
    }
}
