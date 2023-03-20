package net.mysite.SocialMedia.company.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PageDto {

    @JsonProperty("branch")
    private String branch;
    @JsonProperty("mainBranch")
    private String mainBranch;
    @JsonProperty("desc")
    private String desc;
    @JsonProperty("email")
    private String email;
    @JsonProperty("phone")
    private String phone;

    public String getBranch() {
        return branch;
    }

    public String getMainBranch() {
        return mainBranch;
    }

    public String getDesc() {
        return desc;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
