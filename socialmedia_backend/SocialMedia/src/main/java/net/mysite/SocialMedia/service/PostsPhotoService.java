package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.company.sevice.PagePostPhotoService;
import net.mysite.SocialMedia.domain.Posts;
import net.mysite.SocialMedia.domain.PostsPhoto;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.errors.DatabaseException;
import net.mysite.SocialMedia.errors.ImageNotFoundException;
import net.mysite.SocialMedia.errors.UserNotFoundException;
import net.mysite.SocialMedia.repository.PostRepository;
import net.mysite.SocialMedia.repository.PostsPhotoRepository;
import net.mysite.SocialMedia.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.persistence.EntityNotFoundException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import static net.mysite.SocialMedia.company.constants.PagepostvideoConstants.*;
import static net.mysite.SocialMedia.company.constants.PagepostvideoConstants.BYTES;

@Service
public class PostsPhotoService {

    @Autowired
    private PostsPhotoRepository postsPhotoRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(PagePostPhotoService.class);
    private static final String IMAGE_TYPE = "image/";


    public PostsPhoto save(Posts newPost, User user, MultipartFile myFile) throws IOException {
        PostsPhoto newPostImg = new PostsPhoto();
        newPostImg.setPosts(newPost);

        if(myFile != null){
            largeFileHandler(newPostImg, user, myFile);
            newPostImg.setMyFileName(myFile.getOriginalFilename());
            newPostImg.setType(myFile.getContentType());
        }
        try{
            return postsPhotoRepository.save(newPostImg);
        }
        catch (DataAccessException e){
            throw new DatabaseException("Error saving PostsPhoto object to the database.");
        }
    }

    public PostsPhoto getById(Long postId) {
        return postsPhotoRepository.findById(postId).orElseThrow(() -> new ImageNotFoundException("Image not found with ID: " + postId));
    }

    private void largeFileHandler(PostsPhoto postMedia, User user, MultipartFile myFile) throws IOException{

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

        String contentType = myFile.getContentType();
        String fileContent = contentType.split("/")[0];
        String uniqueFileName = dateTimeConverter() + "_" + myFile.getOriginalFilename();
        String mediaDirPath = preMediaFile.getPath() + File.separator + fileContent + "s";
        String currPath = mediaDirPath + File.separator + uniqueFileName;

        postMedia.setPath(currPath);
        postMedia.setMyFileName(myFile.getOriginalFilename());
        postMedia.setType(contentType);

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

    public ResponseEntity<byte[]> getVideoById(Long postId, String range) throws IOException{
        PostsPhoto currVideo = postsPhotoRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Video was not found"));

        long startRange = 0;
        long endRange = CHUNK_SIZE;
        final long fileSize = getFileSize(currVideo.getPath());

        if(range != null){
            String[] ranges = range.split("-");
            startRange = Long.parseLong(ranges[0].substring(6));
            if(ranges.length > 1){
                endRange = Long.parseLong(ranges[1]);
            }
            else{
                endRange = startRange + CHUNK_SIZE;
            }
                endRange = Math.min(endRange, fileSize - 1);
        }

        try{
            byte[] data = readByteRange(currVideo.getPath().substring(currVideo.getPath().lastIndexOf("\\")+1), startRange, endRange, currVideo);

            final String contentLength = String.valueOf((endRange - startRange) + 1);
            HttpStatus httpStatus = HttpStatus.PARTIAL_CONTENT;

            if(endRange+1 >= fileSize){ httpStatus = HttpStatus.OK; }

            return ResponseEntity.status(httpStatus)
                    .header(CONTENT_TYPE, currVideo.getType())
                    .header(ACCEPT_RANGES, BYTES)
                    .header(CONTENT_LENGTH, contentLength)
                    .header(CONTENT_RANGE, BYTES + " " + startRange + "-" + endRange + "/" + fileSize)
                    .body(data);
        }
        catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private byte[] readByteRange(String fileName, long start, long end, PostsPhoto currMedia) throws IOException {
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

    private String dateTimeConverter(){
        LocalDateTime currTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String str = currTime.format(formatter);
        str = str.replace("-", "");
        str = str.replace(":", "");
        str = str.replace(" ", "_");
        return str;
    }

    public Long getFileSize(String path){
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

    public byte[] getImgById(Long postId) {
        PostsPhoto pMedia = getById(postId);

        if(pMedia == null){
            throw new ImageNotFoundException("Image not found for postId: " + postId);
        }

        try{
            String filePath = pMedia.getPath();
            String fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
            System.out.println(pMedia.getPath().substring(pMedia.getPath().lastIndexOf("\\") + 1));
            byte[] imgByte = readByteRange(fileName, 0, getFileSize(pMedia.getPath())-1, pMedia);
            return imgByte;
        }
        catch (IOException e){
            logger.error("Failed to read image with postId: {}", postId, e);
            throw new RuntimeException("Failed to read image for postId: " + postId);
        }
    }

    public Set<PostsPhoto> findAllImg(User user) {
        Set<PostsPhoto> tempList = postsPhotoRepository.findAllImg(user);

        return tempList.stream()
                .filter(p -> p.getType().startsWith(IMAGE_TYPE))
                .collect(Collectors.toSet());
    }

    public Set<PostsPhoto> findAllImgHandler(Long userId) {

        if(!userRepository.existsById(userId)){
            throw new UserNotFoundException("User with Id: " + userId + " not found.");
        }
        Set<PostsPhoto> tempList = postsPhotoRepository.getAllImgWithUserId(userId);

        return tempList.stream()
                .filter(p -> p.getType().startsWith(IMAGE_TYPE))
                .collect(Collectors.toSet());
    }

    public void mediaUpdateHelper(PostsPhoto postsPhoto, User user, MultipartFile file) throws IOException {
        largeFileHandler(postsPhoto, user, file);
        postsPhotoRepository.save(postsPhoto);
    }
    public void deleteById(Long delId) {
        postsPhotoRepository.deleteById(delId);
    }
}
