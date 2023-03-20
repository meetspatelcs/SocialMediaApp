package net.mysite.SocialMedia.dto;

import net.mysite.SocialMedia.domain.User;

public class UserEditDto {

//    private String firstname;
//    private String lastname;
    private String Bio;
    private String state;
    private String country;
    private String email;
    private String phone;
    private User user;

//    public String getFirstname() {
//        return firstname;
//    }
//
//    public String getLastname() {
//        return lastname;
//    }

    public String getBio() {
        return Bio;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public User getUser() {
        return user;
    }
}
