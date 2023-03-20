package net.mysite.SocialMedia.company.sevice;

import net.mysite.SocialMedia.company.domain.Page;
import net.mysite.SocialMedia.company.domain.PagePostPhoto;
import net.mysite.SocialMedia.company.domain.PageThumbnail;
import net.mysite.SocialMedia.company.repository.PageThumbnailRepository;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.errors.ImageNotFoundException;
import net.mysite.SocialMedia.errors.NotFoundException;
import net.mysite.SocialMedia.errors.ThumbnailCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PageThumbnailService {

    @Autowired
    private PageThumbnailRepository pageThumbnailRepository;

    private final Logger logger = LoggerFactory.getLogger(PagePostPhotoService.class);
    public byte[] getThumbnail(Long pageId) {
        PageThumbnail currThumbnail = pageThumbnailRepository.findById(pageId).orElseThrow(() -> new ImageNotFoundException("Thumbnail not found for ID: " + pageId));

        String filePath = currThumbnail.getPath();
        if(filePath == null || filePath.isEmpty()){
            throw new NullPointerException("File path is null or empty for thumbnail img.");
        }
        String fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);

        try{
            byte[] imgByte = readByteRange(fileName, 0, getFileSize(filePath)-1, currThumbnail);
            return imgByte;
        }
        catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException("Failed to read img for thumbnail: " + currThumbnail.getId());
        }
    }

    public PageThumbnail save(Long pageId, MultipartFile myFile, User user) throws IOException {
        PageThumbnail currPageThumbnail = pageThumbnailRepository.findById(pageId).orElseThrow(() -> new FileNotFoundException("Failed to find page thumbnail."));

        if(myFile != null){
            currPageThumbnail.setFileName(myFile.getOriginalFilename());
            currPageThumbnail.setFileType(myFile.getContentType());
            largeFileHandler(myFile, user, currPageThumbnail);
        }

        pageThumbnailRepository.save(currPageThumbnail);
        return currPageThumbnail;
    }

    private void largeFileHandler(MultipartFile myFile, User user, PageThumbnail currPageThumbnail) throws IOException {
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

        String compDirName = currPageThumbnail.getPage().getId().toString();
        File pageFile = new File(prePageFile + File.separator + compDirName);
        if(!pageFile.exists()){
            pageFile.mkdir();
        }

        String thumbDirName = "thumbnails";
        File preThumbnail = new File(pageFile + File.separator + thumbDirName);
        if(!preThumbnail.exists()){
            preThumbnail.mkdir();
        }

        String contentType = myFile.getContentType();
        String uniqueFileName = dateTimeConverter() + "_" + myFile.getOriginalFilename();
        String thumbDirPath = preThumbnail.getPath() + File.separator + uniqueFileName;

        currPageThumbnail.setFileType(contentType);
        currPageThumbnail.setPath(thumbDirPath);
        currPageThumbnail.setFileName(myFile.getOriginalFilename());

        File thumbFile = new File(thumbDirPath);
        try(BufferedInputStream fileInputStream = new BufferedInputStream(myFile.getInputStream());
            FileOutputStream outputStream = new FileOutputStream(thumbFile)) {
            int count;
            while ((count = fileInputStream.read(bufferedBytes)) != -1) {
                outputStream.write(bufferedBytes, 0, count);
            }
        }
    }

    private byte[] readByteRange(String fileName, long start, long end, PageThumbnail currThumbnail) throws IOException {
        Path path = Paths.get(currThumbnail.getPath());

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

    public void save(Page newPage) {
        try{
            PageThumbnail newThumbnail = new PageThumbnail();
            newThumbnail.setPage(newPage);
            pageThumbnailRepository.save(newThumbnail);
        }
        catch (Exception e){
            throw new ThumbnailCreationException("Failed to create thumbnail for page.");
        }
    }

    public PageThumbnail removeThumbnail(Long pageId) {
        PageThumbnail pageThumbnail = pageThumbnailRepository.findById(pageId).orElseThrow(() -> new NotFoundException("Page thumbnail has not has been set."));

        try{
            pageThumbnail.setFileType(null);
            pageThumbnail.setFileName(null);
            pageThumbnail.setPath(null);
            return pageThumbnailRepository.save(pageThumbnail);
        }
        catch (Exception e){
            throw new RuntimeException("An error occurred.");
        }
    }
}
