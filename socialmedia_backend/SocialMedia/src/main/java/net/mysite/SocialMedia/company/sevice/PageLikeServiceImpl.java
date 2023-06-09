package net.mysite.SocialMedia.company.sevice;

import net.mysite.SocialMedia.company.domain.PageLikes;
import net.mysite.SocialMedia.company.domain.PagePost;
import net.mysite.SocialMedia.company.repository.PageLikeRepository;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.err.PagePostLikeException;
import net.mysite.SocialMedia.err.PagePostLikeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PageLikeServiceImpl implements PageLikeService{

    @Autowired
    private PageLikeRepository pageLikeRepository;

    @Override
    public PageLikes increasePagePostLike(PagePost pagePost, User user) {
        PageLikes newLike = new PageLikes();
        newLike.setPagePost(pagePost);
        newLike.setUser(user);
        pageLikeRepository.save(newLike);
        return newLike;
    }

    @Override
    public PageLikes checkIfPagePostIsLiked(PagePost pagePost, User user) throws PagePostLikeNotFoundException {
        PageLikes pageLikes = pageLikeRepository.findPagePostLikesByPagePostAndUser(pagePost, user);
        if(pageLikes == null) { throw new PagePostLikeNotFoundException("Could not find like on the page post."); }
        return pageLikeRepository.findPagePostLikesByPagePostAndUser(pagePost, user);
    }

    @Override
    public Boolean decreasePagePostLike(Long pagePostId, User user) throws PagePostLikeException {
        int numRecords = pageLikeRepository.deleteLikeByPostRefAndUser(pagePostId, user);
        if(numRecords == 0){ throw new PagePostLikeException("Something went wrong."); }
        return true;
    }
}
