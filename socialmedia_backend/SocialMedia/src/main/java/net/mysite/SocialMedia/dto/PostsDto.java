package net.mysite.SocialMedia.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.multipart.MultipartFile;

public class PostsDto {

    private String description;
    private Long id;
    private MultipartFile myFile;



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MultipartFile getMyFile() {
        return myFile;
    }

    public void setMyFile(MultipartFile myFile) {
        this.myFile = myFile;
    }

    @Override
    public String toString() {
        return "PostsDto{" +
                "description='" + description + '\'' +
                ", id=" + id +
                '}';
    }

}
