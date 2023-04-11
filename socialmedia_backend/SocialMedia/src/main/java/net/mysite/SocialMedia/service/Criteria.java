package net.mysite.SocialMedia.service;

import java.util.Map;


public interface Criteria {
    Map<Long, Integer> filterCriteria(Map<Long, Integer> UserSuggestionMap, Long userID);
}
