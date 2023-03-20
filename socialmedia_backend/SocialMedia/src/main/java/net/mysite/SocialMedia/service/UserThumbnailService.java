package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.company.sevice.PagePostPhotoService;
import net.mysite.SocialMedia.domain.PostsPhoto;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.domain.UserThumbnail;
import net.mysite.SocialMedia.errors.NotFoundException;
import net.mysite.SocialMedia.repository.UserThumbnailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static net.mysite.SocialMedia.company.constants.PagepostvideoConstants.BYTE_RANGE;

@Service
public class UserThumbnailService {

    private final Logger logger = LoggerFactory.getLogger(PagePostPhotoService.class);

    @Autowired
    private UserThumbnailRepository userThumbnailRepository;

    public void save(User user) {
        UserThumbnail newUserThumb = new UserThumbnail();
        newUserThumb.setUser(user);
        userThumbnailRepository.save(newUserThumb);
    }

    public UserThumbnail updateThumbnail(User user, MultipartFile myFile) {
        UserThumbnail userThumbnail = userThumbnailRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Thumbnail for user not found."));

        try{
            if(myFile != null){
                largeFileHandler(userThumbnail, user, myFile);
                userThumbnailRepository.save(userThumbnail);
            }
            return userThumbnail;
        }
        catch (IOException e){
            throw new RuntimeException("Something went wrong.");
        }
    }

  public byte[] getImgById(User user){
      UserThumbnail userThumbnail = userThumbnailRepository.findById(user.getId())
              .orElseThrow(() -> new NotFoundException("Thumbnail for user not found."));

      try{
          String filePath = userThumbnail.getPath();
          String fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
          byte[] imgByte = readByteRange(fileName, 0, getFileSize(userThumbnail.getPath())-1, userThumbnail);
          return imgByte;
      }
      catch (NullPointerException e){
          throw new NotFoundException("No thumbnail has been set.");
      }
      catch (IOException e){
          throw new RuntimeException("Failed to read thumbnail.");
      }
  }

    private void largeFileHandler(UserThumbnail userThumbnail, User user, MultipartFile myFile) throws IOException{
        byte[] bufferedBytes = new byte[1024];
        String currUser = user.getUsername().replaceAll("[^a-zA-Z0-9]", "");

        String absolutePath = "D://users/";
        File validateDir = new File(absolutePath + currUser);
        if(!validateDir.exists()){
            validateDir.mkdir();
        }

        String mediaDirName = "userposts";
        File preMediaFile = new File(validateDir + File.separator + mediaDirName);
        if(!preMediaFile.exists()){
            preMediaFile.mkdir();
        }

        String thumbnailDirName = "thumbnails";
        File preThumbnailFile = new File(preMediaFile + File.separator + thumbnailDirName);
        if(!preThumbnailFile.exists()){
            preThumbnailFile.mkdir();
        }

        String contentType = myFile.getContentType();
        String uniqueFileName = dateTimeConverter() + "_" + myFile.getOriginalFilename();
        String currPath = preThumbnailFile + File.separator + uniqueFileName;

        userThumbnail.setPath(currPath);
        userThumbnail.setFileType(contentType);
        userThumbnail.setFileName(uniqueFileName);

        File file = new File(currPath);
        try(BufferedInputStream fileInputStream = new BufferedInputStream(myFile.getInputStream());
            FileOutputStream outputStream = new FileOutputStream(file)){
            int count;
            while ((count = fileInputStream.read(bufferedBytes)) != -1){
                outputStream.write(bufferedBytes, 0, count);
            }
        }
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

    private byte[] readByteRange(String fileName, long start, long end, UserThumbnail userThumbnail) throws IOException {
        Path path = Paths.get(userThumbnail.getPath());

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

    private Long getFileSize(String path){
        String fileName = path.substring(path.lastIndexOf("\\") + 1);

        return Optional.ofNullable(fileName)
                .map(file -> Paths.get(path))
                .map(this::sizeFromFile)
                .orElse(0L);
    }

    private Long sizeFromFile(Path path){
        try{
            return Files.size(path);
        }
        catch (IOException e){
            logger.error("Error while getting the file size", e);
        }
        return 0L;
    }

    public UserThumbnail removeThumbnail(User user) {
        UserThumbnail userThumbnail = userThumbnailRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Thumbnail for user not found."));

        try{
            userThumbnail.setPath(null);
            userThumbnail.setFileName(null);
            userThumbnail.setFileType(null);
            userThumbnailRepository.save(userThumbnail);
            return userThumbnail;
        }
        catch (Exception e){
            throw new RuntimeException("Failed to remove.");
        }
    }
}
