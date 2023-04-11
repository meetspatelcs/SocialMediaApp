package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.domain.Info;
import net.mysite.SocialMedia.repository.FriendRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static net.mysite.SocialMedia.company.constants.FriendConstants.Approved_Friend_stat;

public class CriteriaFriends implements Criteria{

    private  FriendRepository friendRepository;
    private  InfoService infoService;

    public CriteriaFriends(FriendRepository friendRepository, InfoService infoService) {
        this.friendRepository = friendRepository;
        this.infoService = infoService;
    }

    @Override
    public Map<Long, Integer> filterCriteria(Map<Long, Integer> userSuggestionMap, Long userID) {
        Set<Long> loggedUsersFriends = getFriendsInLong(userID);
        generateUsersForRecommendation(loggedUsersFriends, userSuggestionMap, userID);

        // TODO: condition, if empty return random, if not get Friends of friends (if empty return random)
        userSuggestionMap.remove(userID);
        userSuggestionMap.keySet().removeIf(loggedUsersFriends::contains);

        return userSuggestionMap;
    }

    private void generateUsersForRecommendation(Set<Long> usersFriends, Map<Long, Integer> userMap, Long userID) {
        Info userInfo = infoService.getUserInfo(userID);
        String userCountry = userInfo.getCountry();

        for(Long friend : usersFriends) { helper_generateRecommendation(userMap, friend, userCountry); }
    }

    private void helper_generateRecommendation(Map<Long, Integer> userMap, Long friendID, String country) {
        Set<Long> usersFriends = getFriendsInLongByUsersCountry(friendID, country);
        usersFriends.forEach(user -> {
            userMap.compute(user, (key, val) -> (val == null) ? 1 : val+1);
        });
    }

    private Set<Long> getFriendsInLongByUsersCountry(Long friendID, String country) {
        Set<Long> friendsInLong = new HashSet<>();
        friendsInLong.addAll(friendRepository.findLongWCountryAllSent(friendID, Approved_Friend_stat, country));
        friendsInLong.addAll(friendRepository.findLongWCountryAllReceived(friendID, Approved_Friend_stat, country));
        return friendsInLong;
    }

    private Set<Long> getFriendsInLong(Long userID) {
        Set<Long> friendsInLong = new HashSet<>();
        friendsInLong.addAll(friendRepository.findLongAllSent(userID, Approved_Friend_stat));
        friendsInLong.addAll(friendRepository.findLongAllReceived(userID, Approved_Friend_stat));
        return friendsInLong;
    }
}