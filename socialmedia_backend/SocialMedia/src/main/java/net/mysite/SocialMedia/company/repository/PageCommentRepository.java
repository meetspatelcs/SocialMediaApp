package net.mysite.SocialMedia.company.repository;

import net.mysite.SocialMedia.company.domain.PageComments;
import net.mysite.SocialMedia.company.domain.PagePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface PageCommentRepository extends JpaRepository<PageComments, Long> {

    @Query("SELECT c FROM PageComments c WHERE (c.pagePost = :pagePost)")
    Set<PageComments> findAllCommentsByPagePost(PagePost pagePost);
}
