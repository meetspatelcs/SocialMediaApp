package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.company.domain.Page;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.domain.UserPage;
import net.mysite.SocialMedia.errors.DatabaseException;
import net.mysite.SocialMedia.errors.NotFoundException;
import net.mysite.SocialMedia.errors.UserPageCreationException;
import net.mysite.SocialMedia.repository.UserPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

@Service
public class UserPageService {

    private static String default_join = "ROLE_USER";
    private static String default_create = "ROLE_ADMIN";

    @Autowired
    private UserPageRepository userPageRepository;

    public UserPage isUserPage(Page page, User user){
        UserPage userPage = userPageRepository.findRoleWithpageuser(page, user);
        if(userPage == null){
            throw new NotFoundException("User's role was not found on page " + page.getCompName());
        }
        return userPage;
    }

    public UserPage save(Page isPage, User user) {

        if(isPage == null || user == null){
            throw new IllegalArgumentException("Invalid input parameters.");
        }

        UserPage newUser = new UserPage();
        newUser.setUser(user);
        newUser.setPage(isPage);
        newUser.setPageRole(default_join);
      return  userPageRepository.save(newUser);
    }

    public Set<Page> generateListofUserFollowPage(User user) {
        Set<Page> tempList = userPageRepository.findAll(user, default_join);
        if(tempList.isEmpty()){
           throw new NotFoundException("The user is not following any pages.");
        }
        for(Page p : tempList){p.setUser(null);}
        return tempList;
    }

    public Set<Page> getListOfPagesUserFollow(Long userId) {
        Set<Page> tempList = userPageRepository.getAllPagesWithUserId(userId, default_join);
        if(tempList.isEmpty()){
            throw new NotFoundException("There are no more pages to follow.");
        }
        for(Page p : tempList){ p.setUser(null); }
        return tempList;
    }

    public Long getFollowCount(Long pageId){
        return userPageRepository.findFollowCount(pageId, default_join);
    }

    public void createUserPage(Page newPage, User user) {
        try{
            UserPage userPage = new UserPage();
            userPage.setUser(user);
            userPage.setPage(newPage);
            userPage.setPageRole(default_create);
            userPageRepository.save(userPage);
        }
        catch (Exception e){
            throw new UserPageCreationException("Failed to create user page.");
        }
    }

    public void unfollowPage(Long pageId, User user) {
        if(pageId == null || user == null){
            throw new IllegalArgumentException("Page Id and user must not be null.");
        }
        try{
            userPageRepository.deleteUserPageByPageIdAndUser(pageId, user);
        }
        catch (DataAccessException e){
            throw new DatabaseException("User is not following this page.");
        }
    }
}
