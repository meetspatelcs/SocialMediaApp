package net.mysite.SocialMedia.company.sevice;

import net.mysite.SocialMedia.company.domain.Page;
import net.mysite.SocialMedia.company.domain.PagePost;
import net.mysite.SocialMedia.company.domain.PagePostPhoto;
import net.mysite.SocialMedia.err.MissingFieldException;
import net.mysite.SocialMedia.err.PageNotFoundException;
import net.mysite.SocialMedia.err.PagePostNotFoundException;

import java.util.Set;

public interface PagePostService {

     PagePost getById(Long pagePostId) throws PagePostNotFoundException;

     PagePost save(Long pageId, String description) throws MissingFieldException;

     void removeById(Long pageId, Long postId) throws PageNotFoundException;

     PagePost UpdatePagePostById(Long postId, String description) throws PagePostNotFoundException;

     Set<PagePost> getAllByPage(Page myPage);

     Set<PagePostPhoto> getMediaInfoList(Page page, String mediaType) throws PagePostNotFoundException;
}
