package net.mysite.SocialMedia.company.sevice;

import net.bytebuddy.asm.Advice;
import net.mysite.SocialMedia.company.domain.Page;
import net.mysite.SocialMedia.company.domain.PagePost;
import net.mysite.SocialMedia.company.domain.PagePostPhoto;
import net.mysite.SocialMedia.company.dto.PagePostDto;
import net.mysite.SocialMedia.company.repository.PagePostPhotoRepository;
import net.mysite.SocialMedia.company.repository.PagePostRepository;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.errors.DatabaseException;
import net.mysite.SocialMedia.errors.ImageNotFoundException;
import net.mysite.SocialMedia.errors.PostNotFoundException;
import org.apache.coyote.Response;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.core.io.InputStreamResource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.*;
import java.net.http.HttpHeaders;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

import static net.mysite.SocialMedia.company.constants.PagepostvideoConstants.*;

@Service
public class PagePostPhotoService {

    @Autowired
    private PagePostPhotoRepository pagePostPhotoRepository;
    @Autowired
    private PagePostRepository pagePostRepository;
    private final Logger logger = LoggerFactory.getLogger(PagePostPhotoService.class);

    public PagePostPhoto save(PagePost newPagePost, MultipartFile myFile, User user) throws IOException {
        PagePostPhoto newPhoto = new PagePostPhoto();
        newPhoto.setPagePost(newPagePost);

        if(myFile != null){
            largeFileHandler(myFile, user, newPhoto);
            newPhoto.setFileType(myFile.getContentType());
            newPhoto.setFileName(myFile.getOriginalFilename());
        }
        try{
            return pagePostPhotoRepository.save(newPhoto);
        }
        catch (DataAccessException e){
            throw new DatabaseException("Error saving PostsPhoto object to the database.");
        }
    }

    private void largeFileHandler(MultipartFile myFile, User user, PagePostPhoto currPhoto) throws IOException{

        byte[] bufferedBytes = new byte[1024];
        String currUser = user.getUsername().replaceAll("[^a-zA-Z0-9]", "");

        String absolutePath = "D://users/";

        File validateDir = new File(absolutePath + currUser);
        if(!validateDir.exists()){
            validateDir.mkdir();
        }

        String mediaDirName = "userpages";
        File prePageFile = new File(validateDir + File.separator + mediaDirName);
        if(!prePageFile.exists()){
            prePageFile.mkdir();
        }

        String compDirName = currPhoto.getPagePost().getPage().getId().toString();
        File pageFile = new File(prePageFile + File.separator + compDirName);
        if(!pageFile.exists()){
            pageFile.mkdir();
        }

        String contentType = myFile.getContentType();
        String fileContent = contentType.split("/")[0];
        String uniqueFileName = dateTimeConverter() + "_" + myFile.getOriginalFilename();
        String mediaDirPath = pageFile.getPath() + File.separator + fileContent + "s";
        String currPath = mediaDirPath + File.separator + uniqueFileName;

        currPhoto.setFileType(contentType);
        currPhoto.setFileName(myFile.getOriginalFilename());
        currPhoto.setPath(currPath);

        File mediaDir = new File(mediaDirPath);
        if(!mediaDir.exists()){
            mediaDir.mkdir();
        }

        File file = new File(currPath);
        try(BufferedInputStream fileInputStream = new BufferedInputStream(myFile.getInputStream());
            FileOutputStream outputStream = new FileOutputStream(file)){
            int count;
            while ((count = fileInputStream.read(bufferedBytes)) != -1){
                outputStream.write(bufferedBytes, 0, count);
            }
        }
    }

    public Set<PagePostPhoto> getAllByPage(Page page) {
        Set<PagePostPhoto> setPhoto = pagePostPhotoRepository.findAllByPage(page);

        for(PagePostPhoto myPhoto : setPhoto){
            myPhoto.setPostByte(null);
        }

        return setPhoto;
    }

    public PagePostPhoto getById(Long pageId, Long postId) {
        return pagePostPhotoRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Failed to find post with ID " + postId));
    }
    // this method helps to update the post, not create
    public void updateSave(PagePostPhoto pagePostPhoto, MultipartFile myFile, User user) throws IOException {

        pagePostPhoto.setFileName(myFile.getOriginalFilename());
        pagePostPhoto.setFileType(myFile.getContentType());
        largeFileHandler(myFile, user, pagePostPhoto);

        pagePostPhotoRepository.save(pagePostPhoto);
    }

    private String dateTimeConverter(){
        LocalDateTime currTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String str = currTime.format(formatter);
        str = str.replace("-", "");
        str = str.replace(":", "");
        str = str.replace(" ", "_");
        return str;
    }

    public ResponseEntity<byte[]> getVideoById(Long pageId, Long pagePostId, String range) throws IOException{
        PagePostPhoto currVideo = pagePostPhotoRepository.findById(pagePostId)
                .orElseThrow(()-> new EntityNotFoundException("video was not found."));

        long startRange = 0;
        long endRange = CHUNK_SIZE;
        final long fileSize = getFileSize(currVideo.getPath());

        if(range != null){
            String[] ranges = range.split("-");
            startRange = Long.parseLong(ranges[0].substring(6));

            if(ranges.length > 1){ endRange = Long.parseLong(ranges[1]); }
            else{ endRange = startRange + CHUNK_SIZE; }
            endRange = Math.min(endRange, fileSize - 1);
        }

        try{
            byte[] data = readByteRange(currVideo.getPath()
                    .substring(currVideo.getPath()
                            .lastIndexOf("\\")+1), startRange, endRange, currVideo);

            final String contentLength = String.valueOf((endRange - startRange) + 1);
            HttpStatus httpStatus = HttpStatus.PARTIAL_CONTENT;

            if(endRange+1 >= fileSize){ httpStatus = HttpStatus.OK; }

            return ResponseEntity.status(httpStatus)
                    .header(CONTENT_TYPE, currVideo.getFileType())
                    .header(ACCEPT_RANGES, BYTES)
                    .header(CONTENT_LENGTH, contentLength)
                    .header(CONTENT_RANGE, BYTES + " " + startRange + "-" + endRange + "/" + fileSize)
                    .body(data);
        }
        catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private byte[] readByteRange(String fileName, long start, long end, PagePostPhoto currMedia) throws IOException {
        Path path = Paths.get(currMedia.getPath());

        try (InputStream inputStream = (Files.newInputStream(path));
             ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream()) {
            byte[] data = new byte[BYTE_RANGE];
            int nRead;

            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                bufferedOutputStream.write(data, 0, nRead);
            }

            bufferedOutputStream.flush();
            byte[] result = new byte[(int) (end - start) + 1];
            System.arraycopy(bufferedOutputStream.toByteArray(), (int) start, result, 0, result.length);
            return result;
        }
    }

    public Long getFileSize(String path){
        String fileName = path.substring(path.lastIndexOf("\\") + 1);

        return Optional.ofNullable(fileName)
                .map(file -> Paths.get(path))
                .map(this::sizeFromFile)
                .orElse(0L);
    }

    private Long sizeFromFile(Path path){
        try{ return Files.size(path); }
        catch (IOException e){ logger.error("Error while getting the file size", e); }
        return 0L;
    }

    public byte[] getImgById(Long pageId, Long postId)  {

        PagePostPhoto pPost = getById(pageId, postId);
        if(pPost == null){ throw new ImageNotFoundException("Failed to find ref to pagePost with ID " + postId); }

        try{
            String filePath = pPost.getPath();
            String fileName = filePath.substring(filePath.lastIndexOf("\\")+1);
            byte[] imgByte = readByteRange(fileName, 0, getFileSize(pPost.getPath())-1, pPost);
            return imgByte;
        }
        catch (IOException e){
            logger.error("PagePost Entity: Failed to read image with ID" + postId);
            throw new RuntimeException("Failed to read image for postId " + postId);
        }
    }
}
