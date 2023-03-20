package net.mysite.SocialMedia.company.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.mysite.SocialMedia.domain.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class PageComments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdOn;

    @Column(columnDefinition = "TEXT")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private PagePost pagePost;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
