package net.mysite.SocialMedia.company.sevice;

import net.mysite.SocialMedia.company.domain.Page;
import net.mysite.SocialMedia.company.domain.PagePost;
import net.mysite.SocialMedia.company.domain.PagePostPhoto;
import net.mysite.SocialMedia.company.dto.PagePostDto;
import net.mysite.SocialMedia.company.repository.PagePostPhotoRepository;
import net.mysite.SocialMedia.company.repository.PagePostRepository;
import net.mysite.SocialMedia.company.repository.PageRepository;
import net.mysite.SocialMedia.errors.DatabaseException;
import net.mysite.SocialMedia.errors.PostNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PagePostService {

    @Autowired
    private PagePostRepository pagePostRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private PagePostPhotoRepository pagePostPhotoRepository;

    public PagePost getById(Long pagePostId){
        return pagePostRepository.findById(pagePostId).orElseThrow(() -> new PostNotFoundException("Page Post was not found. updatePagePostById"));
    }

    public PagePost save(Long pageId, String description) {
        try{
            PagePost newPagePost = new PagePost();
            newPagePost.setDescription(description);
            newPagePost.setCreatedOn(LocalDateTime.now());
            Page myPage = pageRepository.findById(pageId).get();
            newPagePost.setPage(myPage);

            return pagePostRepository.save(newPagePost);
        }
        catch (DataAccessException e){
            throw new DatabaseException("Error saving Posts object to the database.");
        }
    }

    public void removeById(Long pageId, Long postId) {
        pagePostPhotoRepository.deleteById(postId);

        Page page = pageRepository.findById(pageId).get();
        pagePostRepository.deleteByIds(page, postId);
    }

    public PagePost UpdatePagePostById(Long postId, String description) {
        PagePost pagePost = pagePostRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Page Post was not found. updatePagePostById"));
        pagePost.setDescription(description);
        pagePost.setCreatedOn(LocalDateTime.now());
        return pagePostRepository.save(pagePost);
    }

    public Set<PagePost> getAllByPage(Page myPage) {
        Set<PagePost> allPagePosts = pagePostRepository.findAllByPage(myPage);

        if(allPagePosts.isEmpty()){
            throw new NullPointerException("There are no posts posted on page " + myPage.getId() + " yet.");
        }
        return allPagePosts;
    }

    public Set<PagePostPhoto> getMediaInfoList(Page page, String mediaType) {

        Set<PagePostPhoto> tempPostMedia = pagePostPhotoRepository.findAllByPage(page);

        if(tempPostMedia.isEmpty()){
            throw new NullPointerException("No page post photo content.");
        }
        tempPostMedia.removeIf(elem -> !elem.getFileType().split("/")[0].equals(mediaType));

       return tempPostMedia;
    }
}
