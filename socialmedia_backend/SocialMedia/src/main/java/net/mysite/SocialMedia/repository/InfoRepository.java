package net.mysite.SocialMedia.repository;

import net.mysite.SocialMedia.domain.Info;
import net.mysite.SocialMedia.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InfoRepository extends JpaRepository<Info,Long> {

}
