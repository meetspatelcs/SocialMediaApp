package net.mysite.SocialMedia.company.sevice;

import net.mysite.SocialMedia.company.domain.Page;
import net.mysite.SocialMedia.company.domain.PageThumbnail;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.err.PageNotFoundException;
import net.mysite.SocialMedia.err.PageThumbnailNotFoundException;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;

public interface PageThumbnailService {

     byte[] getThumbnail(Long pageId) throws IOException, PageThumbnailNotFoundException, NullPointerException;

     PageThumbnail save(Long pageId, MultipartFile myFile, User user) throws IOException, PageThumbnailNotFoundException;

     void save(Page newPage) throws PageNotFoundException;

     PageThumbnail removeThumbnail(Long pageId) throws PageNotFoundException, PageThumbnailNotFoundException;
}
