package net.mysite.SocialMedia.repository;

import net.mysite.SocialMedia.domain.UserThumbnail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserThumbnailRepository extends JpaRepository<UserThumbnail, Long> {
}
