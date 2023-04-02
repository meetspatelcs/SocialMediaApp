package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.domain.User;

import java.util.*;

public interface FriendRecommendationService {

    Set<User> generateNewFriendRecommendations(User user);
}

