package net.mysite.SocialMedia.company.sevice;

import net.mysite.SocialMedia.company.domain.Page;
import net.mysite.SocialMedia.company.repository.PageRepository;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.err.PageNotFoundException;
import net.mysite.SocialMedia.repository.FriendRepository;
import net.mysite.SocialMedia.repository.UserPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import static net.mysite.SocialMedia.company.constants.PageConstants.page_recommendation_limit;

@Service
public class PageRecommendationServiceImpl implements PageRecommendationService{

    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private UserPageRepository userPageRepository;
    @Autowired
    private PageRepository pageRepository;

    @Override
    public Set<Page> generatePageSuggestionSet(User user) {
        Long userId = user.getId();
        Map<Long, Integer> pageRecommendationMap = new HashMap<>();

        PageCriteriaFriends friends = new PageCriteriaFriends(friendRepository, userPageRepository);
        Map<Long, Integer> friendsPages = friends.filterPageCriteria(pageRecommendationMap, userId);

        //TODO: when this is empty, create a random set to return which user is not following
        if(friendsPages.isEmpty()){ System.out.println("Pages of friends are null."); }

        return generateSetOfPages(friendsPages);
    }

    private Set<Page> generateSetOfPages(Map<Long, Integer> friendsPages) {
        Set<Page> recommendationSet = new HashSet<>();
        if(friendsPages.size() <= page_recommendation_limit){
            friendsPages.keySet().forEach(page -> { addPageInSet(recommendationSet, page); });
        }
        helper_generatePageRecommendations(recommendationSet, friendsPages);
        return recommendationSet;
    }

    private void addPageInSet(Set<Page> recommendationSet, Long pageId) {
        recommendationSet.add(pageRepository.findById(pageId)
                .orElseThrow(() -> new PageNotFoundException("Failed to add page. Could not find page.")));
    }

    private void helper_generatePageRecommendations(Set<Page> pageSet, Map<Long, Integer> pageMap) {
        List<Long>[] temp = new List[6];
        pageMap.forEach((pageId, freq) -> {
            if(freq > 4){ handlePageList(temp, 5, pageId); }
            else{ handlePageList(temp, freq, pageId); }
        });

        int count = 0;
        for(int i = temp.length - 1; i >= 0; i--){
            if(temp[i] != null){
                for(int j = 0; j < temp[i].size(); j++){
                    if(count < page_recommendation_limit){
                        addPageInSet(pageSet, temp[i].get(j));
                        count++;
                    }
                }
            }
            if(count == page_recommendation_limit){
                break;
            }
        }
    }

    private void handlePageList(List<Long>[] temp, int i, Long pageId){
        if(temp[i] == null) { temp[i] = new ArrayList<>(); }
        temp[i].add(pageId);
    }
}