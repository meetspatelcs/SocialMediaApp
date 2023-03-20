package net.mysite.SocialMedia.dto;

public class CommentDto {

    private Long id;
    private Long post;
    private String text;
    private String user;

    public Long getId() {
        return id;
    }

    public Long getPost() {
        return post;
    }

    public String getText() {
        return text;
    }

    public String getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "CommentDto{" +
                "id=" + id +
                ", post=" + post +
                ", text='" + text + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
