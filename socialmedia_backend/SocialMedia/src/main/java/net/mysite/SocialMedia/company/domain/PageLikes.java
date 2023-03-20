package net.mysite.SocialMedia.company.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.mysite.SocialMedia.domain.User;

import javax.persistence.*;

@Entity
public class PageLikes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_post_id")
    @JsonIgnore
    private PagePost pagePost;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PagePost getPagePost() {
        return pagePost;
    }

    public void setPagePost(PagePost pagePost) {
        this.pagePost = pagePost;
    }
}
