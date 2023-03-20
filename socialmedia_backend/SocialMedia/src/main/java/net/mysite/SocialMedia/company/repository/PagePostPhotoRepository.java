package net.mysite.SocialMedia.company.repository;

import net.mysite.SocialMedia.company.domain.Page;
import net.mysite.SocialMedia.company.domain.PagePostPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface PagePostPhotoRepository extends JpaRepository<PagePostPhoto, Long> {
    @Query("SELECT ph FROM PagePostPhoto ph, PagePost pp WHERE pp.page = :page")
    Set<PagePostPhoto> findAllByPage(Page page);
}
