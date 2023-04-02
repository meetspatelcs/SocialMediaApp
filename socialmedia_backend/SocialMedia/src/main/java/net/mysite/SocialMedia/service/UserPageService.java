package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.company.domain.Page;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.domain.UserPage;
import net.mysite.SocialMedia.err.PageNotFoundException;
import net.mysite.SocialMedia.err.PageRoleNotFoundException;
import net.mysite.SocialMedia.err.UserNotFoundException;

import java.util.Set;

public interface UserPageService {
     UserPage isUserPage(Page page, User user) throws PageRoleNotFoundException;

     UserPage save(Page isPage, User user) throws PageNotFoundException;

     Set<Page> generateListofUserFollowPage(User user);

     Set<Page> getListOfPagesUserFollow(Long userId) throws UserNotFoundException;

     Long getFollowCount(Long pageId);

     void createUserPage(Page newPage, User user) throws PageNotFoundException;

     void unfollowPage(Long pageId, User user) throws IllegalArgumentException;
}
