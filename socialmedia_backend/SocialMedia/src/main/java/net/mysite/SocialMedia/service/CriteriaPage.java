package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.repository.UserPageRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static net.mysite.SocialMedia.company.constants.FriendConstants.SAME_PAGE_MAX;
import static net.mysite.SocialMedia.company.constants.PageConstants.default_join;

public class CriteriaPage implements Criteria{

    private UserPageRepository userPageRepository;

    public CriteriaPage(UserPageRepository userPageRepository){
        this.userPageRepository = userPageRepository;
    }

    @Override
    public Map<Long, Integer> filterCriteria(Map<Long, Integer> userSuggestionMap, Long userID) {
        Set<Long> usersPages = userPageRepository.findLongPageByUser(userID, default_join);
        if(usersPages.isEmpty()) { System.out.println("User does not follow any Pages."); return userSuggestionMap; }

        Set<Long> removeUsers = new HashSet<>();
        int mapSize = userSuggestionMap.size();
        userSuggestionMap.keySet().forEach(user -> {
            int removeUsersSetSize = removeUsers.size();
            int count = userPageRepository.countCommonPages(user, usersPages, default_join);

            if(count == 0 && (mapSize - removeUsersSetSize) > SAME_PAGE_MAX) { removeUsers.add(user); }
            else{
                userSuggestionMap.compute(user, (key, val) -> (val == null) ? count : val+count);
        }});

        if(!removeUsers.isEmpty()) { userSuggestionMap.keySet().removeIf(removeUsers::contains); }
        return userSuggestionMap;
    }
}
