package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.company.domain.Page;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.domain.UserPage;
import net.mysite.SocialMedia.err.PageNotFoundException;
import net.mysite.SocialMedia.err.PageRoleNotFoundException;
import net.mysite.SocialMedia.err.UserNotFoundException;
import net.mysite.SocialMedia.repository.UserPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
public class UserPageService {

    private static String default_join = "ROLE_USER";
    private static String default_create = "ROLE_ADMIN";
    @Autowired
    private UserPageRepository userPageRepository;
    @Autowired
    private UserService userService;

    public UserPage isUserPage(Page page, User user){
        UserPage pageRole = userPageRepository.findRoleWithpageuser(page, user);
        if(pageRole == null) {throw new PageRoleNotFoundException("Page role does not exists."); }
        return pageRole;
    }

    public UserPage save(Page isPage, User user) {
        if(isPage == null){ throw new PageNotFoundException("Page does not exists."); }

        UserPage newUser = new UserPage();
        newUser.setUser(user);
        newUser.setPage(isPage);
        newUser.setPageRole(default_join);
        return  userPageRepository.save(newUser);
    }

    public Set<Page> generateListofUserFollowPage(User user) {
        Set<Page> tempList = userPageRepository.findAll(user, default_join);
        if(tempList.isEmpty()){ return Collections.emptySet(); }
        for(Page p : tempList){p.setUser(null);}
        return tempList;
    }

    public Set<Page> getListOfPagesUserFollow(Long userId) {
        Optional<User> user = userService.findById(userId);
        if(user.isEmpty()){ throw new UserNotFoundException("User does not exists."); }

        Set<Page> tempList = userPageRepository.getAllPagesWithUserId(userId, default_join);
        if(tempList.isEmpty()){ return Collections.emptySet(); }
        for(Page p : tempList){ p.setUser(null); }
        return tempList;
    }

    public Long getFollowCount(Long pageId){
        return userPageRepository.findFollowCount(pageId, default_join);
    }

    public void createUserPage(Page newPage, User user) {
        if(newPage == null) { throw new PageNotFoundException("Page does not exists."); }

        UserPage userPage = new UserPage();
        userPage.setUser(user);
        userPage.setPage(newPage);
        userPage.setPageRole(default_create);
        userPageRepository.save(userPage);
    }

    public void unfollowPage(Long pageId, User user) {
        if(pageId == null){ throw new IllegalArgumentException("Page Id cannot be null."); }
        userPageRepository.deleteUserPageByPageIdAndUser(pageId, user);
    }
}
