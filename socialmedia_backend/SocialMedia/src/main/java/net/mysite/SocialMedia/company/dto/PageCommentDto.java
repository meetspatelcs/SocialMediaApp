package net.mysite.SocialMedia.company.dto;

public class PageCommentDto {

    private Long id;
    private Long pagePost;
    private String text;
    private String user;

    public Long getId() {
        return id;
    }

    public Long getPagePost() {
        return pagePost;
    }

    public String getText() {
        return text;
    }

    public String getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "PageCommentDto{" +
                "id=" + id +
                ", pagePost=" + pagePost +
                ", text='" + text + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
