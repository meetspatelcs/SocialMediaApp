package net.mysite.SocialMedia.company.dto;

import net.mysite.SocialMedia.company.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public class PagePostDto {

    private Long pagePostId;
    private MultipartFile myFile;

    public Long getPagePostId() {
        return pagePostId;
    }

    public MultipartFile getMyFile() {
        return myFile;
    }
}
