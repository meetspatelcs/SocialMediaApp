package net.mysite.SocialMedia.company.domain;

import javax.persistence.*;

@Entity
public class PagePostPhoto {

    @Id
    private Long id;
    private String fileType;
    private String fileName;
    private String path;
    @Lob
    private byte[] postByte;
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "pagePost_id")
    private PagePost pagePost;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getPostByte() {
        return postByte;
    }

    public void setPostByte(byte[] postByte) {
        this.postByte = postByte;
    }

    public PagePost getPagePost() {
        return pagePost;
    }

    public void setPagePost(PagePost pagePost) {
        this.pagePost = pagePost;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
