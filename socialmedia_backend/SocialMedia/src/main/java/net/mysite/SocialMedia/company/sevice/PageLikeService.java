package net.mysite.SocialMedia.company.sevice;

import net.mysite.SocialMedia.company.domain.PageLikes;
import net.mysite.SocialMedia.company.domain.PagePost;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.err.PagePostLikeException;
import net.mysite.SocialMedia.err.PagePostLikeNotFoundException;

public interface PageLikeService {


     PageLikes increasePagePostLike(PagePost pagePost, User user);

     PageLikes checkIfPagePostIsLiked(PagePost pagePost, User user) throws PagePostLikeNotFoundException;

     Boolean decreasePagePostLike(Long pagePostId, User user) throws PagePostLikeException;
}
