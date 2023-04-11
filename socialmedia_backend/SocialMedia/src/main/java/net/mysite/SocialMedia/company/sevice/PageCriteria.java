package net.mysite.SocialMedia.company.sevice;

import java.util.Map;

public interface PageCriteria {

    Map<Long, Integer> filterPageCriteria(Map<Long, Integer> pageSuggestionMap, Long userID);
}
