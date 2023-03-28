package net.mysite.SocialMedia.domain;

import javax.persistence.*;

@Entity
public class PostsPhoto {

    @Id
    private Long id;
    private String type; // type of uploaded file
    private String myFileName; // name of uploaded file

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "posts_id")
    private Posts posts;
    private String path;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMyFileName() {
        return myFileName;
    }

    public void setMyFileName(String myFileName) {
        this.myFileName = myFileName;
    }

    public Posts getPosts() {
        return posts;
    }

    public void setPosts(Posts posts) {
        this.posts = posts;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
