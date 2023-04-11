package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.domain.Info;
import net.mysite.SocialMedia.repository.InfoRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static net.mysite.SocialMedia.company.constants.FriendConstants.SAME_REGION_MAX;

public class CriteriaRegion implements Criteria{

    private InfoRepository infoRepository;

    public CriteriaRegion(InfoRepository infoRepository){
        this.infoRepository = infoRepository;
    }

    @Override
    public Map<Long, Integer> filterCriteria(Map<Long, Integer> userSuggestionMap, Long userID) {
        Info userInfo = infoRepository.findById(userID)
                .orElseThrow(() -> new NoSuchElementException("Could not find information about user."));
        String state = userInfo.getState();
        int mapSize = userSuggestionMap.size();

        Set<Long> removeUsers = new HashSet<>();
        userSuggestionMap.keySet().forEach(user -> {
            String userState = infoRepository.findStateByUserId(user);
            int removeUserSetSize = removeUsers.size();

            if((mapSize - removeUserSetSize) > SAME_REGION_MAX && isStateSame(userState, state)) { removeUsers.add(user); }
            else{
                if(isStateSame(userState, state)) { userSuggestionMap.compute(user, (key, val) -> (val == null) ? 1 : val+1); }
                else{ userSuggestionMap.compute(user, (key, val) -> (val == null) ? 2 : val+2); }
        }});

        if(!removeUsers.isEmpty()) { userSuggestionMap.keySet().removeIf(removeUsers::contains); }
        return userSuggestionMap;
    }

    private boolean isStateSame(String userState, String state){
        return !userState.equals(state);
    }
}
