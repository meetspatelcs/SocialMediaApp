package net.mysite.SocialMedia.repository;

import net.mysite.SocialMedia.company.domain.Page;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.domain.UserPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

public interface UserPageRepository extends JpaRepository<UserPage, Long> {

    @Query("SELECT u FROM UserPage u WHERE (u.page = :page AND u.user = :user)")
    UserPage findRoleWithpageuser(Page page, User user);

    @Query("SELECT u.page FROM UserPage u WHERE (u.user = :user AND u.pageRole = :default_join)")
    Set<Page> findAll(User user, String default_join);

    @Query("SELECT u.page FROM UserPage u WHERE (u.user.id = :userId AND u.pageRole = :default_join)")
    Set<Page> getAllPagesWithUserId(Long userId, String default_join);
    @Query("SELECT u.page FROM UserPage u WHERE (u.user = :user AND u.pageRole = :role_user)")
    Set<Page> findPageWithUser(User user, String role_user);

    @Query("SELECT COUNT(u) FROM UserPage u WHERE (u.page.id = :pageId AND u.pageRole = :default_join)")
    Long findFollowCount(Long pageId, String default_join);
    @Modifying
    @Transactional
    @Query("DELETE FROM UserPage u WHERE (u.page.id = :pageId AND u.user = :user)")
    void deleteUserPageByPageIdAndUser(Long pageId, User user);

    @Query("SELECT p.page.id FROM UserPage p WHERE (p.user.id = :userID AND p.pageRole = :default_join)")
    Set<Long> findLongPageByUser(Long userID, String default_join);
}
