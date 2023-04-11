package net.mysite.SocialMedia.company.sevice;

import net.mysite.SocialMedia.company.domain.Page;
import net.mysite.SocialMedia.domain.User;

import java.util.Set;

public interface PageRecommendationService {

    Set<Page> generatePageSuggestionSet(User user);
}
