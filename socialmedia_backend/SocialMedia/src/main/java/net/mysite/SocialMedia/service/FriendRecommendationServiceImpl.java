package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.domain.Info;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.err.UserNotFoundException;
import net.mysite.SocialMedia.repository.FriendRepository;
import net.mysite.SocialMedia.repository.UserPageRepository;
import net.mysite.SocialMedia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static net.mysite.SocialMedia.company.constants.FriendConstants.Approved_Friend_stat;
import static net.mysite.SocialMedia.company.constants.FriendConstants.recommendation_limit;
import static net.mysite.SocialMedia.company.constants.PageConstants.default_join;

@Service
public class FriendRecommendationServiceImpl implements FriendRecommendationService{

    @Autowired
    private UserPageRepository userPageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private InfoService infoService;

    @Override
    public Set<User> generateNewFriendRecommendations(User user) {
        Set<Long> userFriendSet = getFriendListInLong(user.getId());
        Map<Long, Integer> recommendationMap = new HashMap<>();

        for(Long friend : userFriendSet){ generateFriendsSetOfUsers(friend, recommendationMap); }

        Long userID = user.getId();

        defaultFilter(recommendationMap, userFriendSet, userID);
        filterUserWithPage(recommendationMap, userID);
        filterUserWithRegion(recommendationMap, userID);

        Set<User> temp = convertMapToSet(recommendationMap);
        return new HashSet<>(temp);
    }

    private Set<Long> getFriendListInLong(Long userID) {
        Set<Long> tempFriendSet = new HashSet<>();
        tempFriendSet.addAll(friendRepository.findLongAllSent(userID, Approved_Friend_stat));
        tempFriendSet.addAll(friendRepository.findLongAllReceived(userID, Approved_Friend_stat));
        return tempFriendSet;
    }

    private void generateFriendsSetOfUsers(Long user, Map<Long, Integer> userFrequencyMap){
        Set<Long> tempFriendSet = getFriendListInLong(user);
        MutualFriendFrequencyMapper(tempFriendSet, userFrequencyMap);
    }

    private void MutualFriendFrequencyMapper(Set<Long> userSet, Map<Long, Integer> userFrequencyMap){
        for(Long user : userSet){
            userFrequencyMap.put(user, userFrequencyMap.getOrDefault(user, 0)+1);
        }
    }

    private void defaultFilter(Map<Long, Integer> map, Set<Long> friendSet, Long userID){
        map.remove(userID); // removes the logged user
        map.entrySet().removeIf(entry -> friendSet.contains(entry.getKey())); // removes logged users friends
    }

    private void filterUserWithPage(Map<Long, Integer> map, Long userID){
        Set<Long> userPageSet = userPageRepository.findLongPageByUser(userID, default_join);
        if(userPageSet.isEmpty()) {return;}
        Set<Long> tempRemoveUser = new HashSet<>();

        for(Long eachUserID : map.keySet()){
            Set<Long> recommendedUserPageSet = userPageRepository.findLongPageByUser(eachUserID, default_join);

            if(recommendedUserPageSet.isEmpty() && map.size() > recommendation_limit) { tempRemoveUser.add(eachUserID); }
            else{ filterHelper_recommendationUpdater(map, userPageSet, recommendedUserPageSet, eachUserID); }
        }

        if(!tempRemoveUser.isEmpty()) { map.keySet().removeIf(tempRemoveUser::contains); }
    }

    private void filterHelper_recommendationUpdater(Map<Long, Integer> map, Set<Long> userPage, Set<Long> otherUserPageSet, Long userID) {
        int count = 0;

        for(Long othersPage : otherUserPageSet){ if(userPage.contains(othersPage)){ count++; } }

        if(count == 0 && map.size() > recommendation_limit){ map.remove(userID); }
        else{ map.put(userID, map.getOrDefault(userID, 0)+count); }
    }

    private void filterUserWithRegion(Map<Long, Integer> recommendationMap, Long userID) {
        Info userInfo = infoService.getUserInfo(userID);

        String userCountry = userInfo.getCountry();
        String userState = userInfo.getState();

        Set<Info> otherInfoSet = new HashSet<>();
        for(Long eachUser : recommendationMap.keySet()){ otherInfoSet.add(infoService.getUserInfo(eachUser)); }

        for(Info eachUserInfo : otherInfoSet){
            if(recommendationMap.size() > recommendation_limit && (!userCountry.equalsIgnoreCase(eachUserInfo.getCountry()))){
                recommendationMap.remove(eachUserInfo.getUser().getId());
            }
            else {
                if(userCountry.equalsIgnoreCase(eachUserInfo.getCountry()) && userState.equalsIgnoreCase(eachUserInfo.getState())){
                    recommendationMap.put(eachUserInfo.getUser().getId(), recommendationMap.getOrDefault(eachUserInfo.getUser().getId(), 0) +2);
                }
                else if(userCountry.equalsIgnoreCase(eachUserInfo.getCountry())){
                    recommendationMap.put(eachUserInfo.getUser().getId(), recommendationMap.getOrDefault(eachUserInfo.getUser().getId(), 0) +1);
                }
            }
        }
    }

    private Set<User> convertMapToSet(Map<Long, Integer> recommendationMap) {
        Set<User> recommendationSet = new HashSet<>();
        if(recommendationMap.isEmpty()) {
            return recommendationSet;
        }
        else if(recommendationMap.size() <= recommendation_limit){
            for(Long user: recommendationMap.keySet()){
                recommendationSet.add(userRepository.findById(user)
                        .orElseThrow(() -> new UserNotFoundException("User does not exists.")));
            }
            return recommendationSet;
        }
        helper_GenerateUserRecommendation(recommendationSet, recommendationMap);
        return recommendationSet;
    }

    private void helper_GenerateUserRecommendation(Set<User> recommendationSet, Map<Long, Integer> recommendationMap) {

        List<Long>[] temp = new List[6]; // 5 = points gained via frequency + 0
        for(Long userId : recommendationMap.keySet()){

            int freq = recommendationMap.get(userId);
            if(temp[freq] == null){ temp[freq] = new ArrayList<>(); }

            if(temp[5].size() + temp[4].size() >= recommendation_limit){ break; }

            else if(freq > 4){ temp[5].add(userId); }
            else{ temp[freq].add(userId); }
        }

        int counter = 0;
        for(int i = temp.length-1; i >= 0; i--){
            if(temp[i] != null){
                for(int j = 0; j < temp[i].size(); j++){
                    if(recommendationSet.size() < recommendation_limit){
                        recommendationSet.add(userRepository.findById(temp[i].get(j))
                                .orElseThrow(() -> new UserNotFoundException("User does not exists.")));
                        counter++;
                    }
                }
            }
            if(counter == recommendation_limit){
                break;
            }
        }
    }
}
