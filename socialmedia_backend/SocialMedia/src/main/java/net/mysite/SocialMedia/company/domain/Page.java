package net.mysite.SocialMedia.company.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.mysite.SocialMedia.domain.Authority;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.domain.UserPage;
import org.hibernate.annotations.Formula;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Entity
@Table(name = "pages")
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonIgnore
    private LocalDate createdOn;
    private String compName;
    private String contactEmail;
    private String contactPhone;
    private String compDesc;
    @ManyToOne(optional = false)
    private User user;

//    @ManyToMany
//    @JoinTable(name = "pages_users", joinColumns = {@JoinColumn(name = "page_id")}, inverseJoinColumns = {@JoinColumn(name = "user_id")})
//    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "page")
    @JsonIgnore
    private Set<UserPage> userPages;

    public Page(){}

    public Page(LocalDate createdOn, String compName, String contactEmail, String contactPhone, String compDesc) {
        this.createdOn = createdOn;
        this.compName = compName;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.compDesc = compDesc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDate createdOn) {
        this.createdOn = createdOn;
    }

    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getCompDesc() {
        return compDesc;
    }

    public void setCompDesc(String compDesc) {
        this.compDesc = compDesc;
    }

//    public Set<User> getUsers() {
//        return users;
//    }
//
//    public void setUsers(Set<User> users) {
//        this.users = users;
//    }


    public Set<UserPage> getUserPages() {
        return userPages;
    }

    public void setUserPages(Set<UserPage> userPages) {
        this.userPages = userPages;
    }
}
