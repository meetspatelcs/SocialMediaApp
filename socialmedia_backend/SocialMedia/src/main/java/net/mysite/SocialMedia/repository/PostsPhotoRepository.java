package net.mysite.SocialMedia.repository;

import net.mysite.SocialMedia.domain.PostsPhoto;
import net.mysite.SocialMedia.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface PostsPhotoRepository extends JpaRepository<PostsPhoto, Long> {

    @Query("SELECT pp FROM PostsPhoto pp JOIN Posts p ON p = pp.posts WHERE p.user = :user")
    Set<PostsPhoto> findAllImg(User user);

    @Query("SELECT pp FROM PostsPhoto pp JOIN Posts p ON p = pp.posts WHERE p.user.id = :userId")
    Set<PostsPhoto> getAllImgWithUserId(Long userId);
}
