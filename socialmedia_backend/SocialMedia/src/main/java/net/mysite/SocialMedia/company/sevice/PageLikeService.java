package net.mysite.SocialMedia.company.sevice;

import net.mysite.SocialMedia.company.domain.PageLikes;
import net.mysite.SocialMedia.company.domain.PagePost;
import net.mysite.SocialMedia.company.repository.PageLikeRepository;
import net.mysite.SocialMedia.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PageLikeService {

    @Autowired
    private PageLikeRepository pageLikeRepository;

    @Autowired
    private PagePostService pagePostService;
    public PageLikes increasePagePostLike(PagePost pagePost, User user) {

        PageLikes newLike = new PageLikes();

        if(pagePost == null){
            System.out.println("im null");
        }
        newLike.setPagePost(pagePost);
        newLike.setUser(user);
        pageLikeRepository.save(newLike);

        return newLike;
    }

    public PageLikes checkIfPagePostIsLiked(PagePost pagePost, User user) {
        return pageLikeRepository.findPagePostLikesByPagePostAndUser(pagePost, user);
    }

    public Boolean decreasePagePostLike(Long pagePostId, User user) {
        pageLikeRepository.deletePageLikeByPagePostIdAndUser(pagePostId, user);

        PagePost pagePost = pagePostService.getById(pagePostId);
        PageLikes pageLikes = checkIfPagePostIsLiked(pagePost, user);

        if(pageLikes != null){
            throw new RuntimeException("failed to delete pagepostlike.");
        }

        return true;
    }
}
