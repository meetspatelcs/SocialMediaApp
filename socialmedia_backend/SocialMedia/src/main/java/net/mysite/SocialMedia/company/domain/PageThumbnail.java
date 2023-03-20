package net.mysite.SocialMedia.company.domain;

import javax.persistence.*;

@Entity
public class PageThumbnail {

    @Id
    private Long id;
    private String fileName;
    private String fileType;
    private String path;
    @Lob
    private byte[] postByte;
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "page_id")
    private Page page;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getPostByte() {
        return postByte;
    }

    public void setPostByte(byte[] postByte) {
        this.postByte = postByte;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
