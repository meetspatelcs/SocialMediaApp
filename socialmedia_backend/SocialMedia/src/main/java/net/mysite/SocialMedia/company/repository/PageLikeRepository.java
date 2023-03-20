package net.mysite.SocialMedia.company.repository;

import net.mysite.SocialMedia.company.domain.PageLikes;
import net.mysite.SocialMedia.company.domain.PagePost;
import net.mysite.SocialMedia.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PageLikeRepository extends JpaRepository<PageLikes, Long> {
    @Query("SELECT l FROM PageLikes l WHERE (l.pagePost = :pagePost AND l.user = :user)")
    PageLikes findPagePostLikesByPagePostAndUser(PagePost pagePost, User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM PageLikes l WHERE (l.pagePost.id = :pagePostId AND l.user = :user)")
    void deletePageLikeByPagePostIdAndUser(Long pagePostId, User user);
}
