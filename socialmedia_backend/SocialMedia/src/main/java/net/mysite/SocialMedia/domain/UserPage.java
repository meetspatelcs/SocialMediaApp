package net.mysite.SocialMedia.domain;

import net.mysite.SocialMedia.company.domain.Page;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class UserPage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "page_id")
    private Page page;
    private String pageRole;

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

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public String getPageRole() {
        return pageRole;
    }

    public void setPageRole(String pageRole) {
        this.pageRole = pageRole;
    }
}