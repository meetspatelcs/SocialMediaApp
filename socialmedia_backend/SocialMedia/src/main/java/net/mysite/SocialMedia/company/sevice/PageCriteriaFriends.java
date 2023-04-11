package net.mysite.SocialMedia.company.sevice;

import net.mysite.SocialMedia.repository.FriendRepository;
import net.mysite.SocialMedia.repository.UserPageRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static net.mysite.SocialMedia.company.constants.FriendConstants.Approved_Friend_stat;
import static net.mysite.SocialMedia.company.constants.PageConstants.default_join;

public class PageCriteriaFriends implements PageCriteria {

    private FriendRepository friendRepository;

    private UserPageRepository userPageRepository;

    public PageCriteriaFriends(FriendRepository friendRepository, UserPageRepository userPageRepository) {
        this.friendRepository = friendRepository;
        this.userPageRepository = userPageRepository;
    }

    @Override
    public Map<Long, Integer> filterPageCriteria(Map<Long, Integer> pageSuggestionMap, Long userID) {

        Set<Long> loggedUserFriends = getFriendsInLong(userID);
        if(loggedUserFriends.isEmpty()){ return pageSuggestionMap; }

        generatePagesForRecommendation(pageSuggestionMap, loggedUserFriends, userID);

        return pageSuggestionMap;
    }

    private void generatePagesForRecommendation(Map<Long, Integer> pageSuggestionMap, Set<Long> loggedUserFriends, Long userID) {
        loggedUserFriends.forEach(user -> {
            Set<Long> userFollowPages = userPageRepository.findLongPageByUser(user, default_join);
            if(!userFollowPages.isEmpty()) { helper_generateRecommendation(pageSuggestionMap, userFollowPages); }
        });

        if(pageSuggestionMap.isEmpty()){ return; }

        Set<Long> loggedUsersPages = userPageRepository.findLongPageByUser(userID, default_join);
        pageSuggestionMap.keySet().removeIf(loggedUsersPages::contains);
    }

    private void helper_generateRecommendation(Map<Long, Integer> pageSuggestionMap, Set<Long> userPages) {
        userPages.forEach(page -> { pageSuggestionMap.compute(page, (key, val) -> (val == null) ? 1 : val+1); });
    }

    private Set<Long> getFriendsInLong(Long userID) {
        Set<Long> friendSet = new HashSet<>();
        friendSet.addAll(friendRepository.findLongAllSent(userID, Approved_Friend_stat));
        friendSet.addAll(friendRepository.findLongAllReceived(userID, Approved_Friend_stat));
        return friendSet;
    }
}