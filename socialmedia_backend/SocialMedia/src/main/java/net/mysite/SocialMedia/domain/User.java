package net.mysite.SocialMedia.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonIgnore
    private LocalDate createdOn;
    private String username;
    @JsonIgnore
    private String password;
    private String firstname;
    private String lastname;
    private String Identification;
    private Date dob;
    private char isActive;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    Set<Authority> authorities = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<UserPage> userPages;

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public User(){}

    public User(LocalDate createdOn, String username, String password, String firstname, String lastname, String Identification, Date dob, char isActive) {
        this.createdOn = createdOn;
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.Identification = Identification;
        this.dob = dob;
        this.isActive = isActive;
        userPages = new HashSet<>();
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
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getIdentification() {
        return Identification;
    }

    public void setIdentification(String Identification) {
        this.Identification = Identification;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public char getIsActive() {
        return isActive;
    }

    public void setIsActive(char isActive) {
        this.isActive = isActive;
    }

    public Set<UserPage> getUserPages() {
        return userPages;
    }

    public void setUserPages(Set<UserPage> userPages) {
        this.userPages = userPages;
    }
}
