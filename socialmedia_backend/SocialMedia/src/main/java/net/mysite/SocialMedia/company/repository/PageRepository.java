package net.mysite.SocialMedia.company.repository;

import net.mysite.SocialMedia.company.domain.Page;
import net.mysite.SocialMedia.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Set;

public interface PageRepository extends JpaRepository<Page, Long> {

    Set<Page> findByUser(User user);
    @Query("SELECT p FROM Page p WHERE LEVENSHTEIN_RATIO(p.compName, :searchTerm) >= 0.5")
    Set<Page> findPagesBySearchTerm(String searchTerm);

//    @Query("SELECT p FROM Page p WHERE p NOT IN (SELECT up.page FROM UserPage up WHERE up.user = :user) ")
    @Query("SELECT p FROM Page p WHERE NOT EXISTS (SELECT up FROM UserPage up WHERE up.user = :user AND up.page = p)")
    Set<Page> findNotFollowedPagesByUser(User user);
}
