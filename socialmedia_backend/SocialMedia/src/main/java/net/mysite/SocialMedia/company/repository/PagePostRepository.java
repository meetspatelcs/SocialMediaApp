package net.mysite.SocialMedia.company.repository;

import net.mysite.SocialMedia.company.domain.Page;
import net.mysite.SocialMedia.company.domain.PagePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface PagePostRepository extends JpaRepository<PagePost, Long> {
    @Query("SELECT pp FROM PagePost pp WHERE pp.page = :myPage ORDER BY pp.id DESC")
    Set<PagePost> findAllByPageForDay(Page myPage);

    Set<PagePost> findAllByPage(Page myPage);

    @Modifying
    @Transactional
    @Query("DELETE FROM PagePost pp WHERE pp.page = :page AND pp.id = :postId")
    void deleteByIds(Page page, Long postId);

    @Query("SELECT pp FROM PagePost pp WHERE (pp.page = :currPage AND pp.createdOn >= :endPoint) ")
    Set<PagePost> findAllByPageTest(Page currPage, LocalDateTime endPoint);
}
