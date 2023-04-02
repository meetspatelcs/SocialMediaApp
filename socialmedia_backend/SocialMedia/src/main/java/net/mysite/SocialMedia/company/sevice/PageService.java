package net.mysite.SocialMedia.company.sevice;

import net.mysite.SocialMedia.company.domain.Page;
import net.mysite.SocialMedia.company.dto.PageDto;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.err.MissingFieldException;
import net.mysite.SocialMedia.err.PageNotFoundException;

import java.util.Set;

public interface PageService {

     Page save(User user, PageDto pageDto) throws MissingFieldException;

     Set<Page> getMyPages(User user);

     Page getById(Long pageId) throws PageNotFoundException;

     Set<Page> getPagesWithSearchTerm(String searchTerm);

     Set<Page> getSetOfPagesNotFollowedByUser(User user);
}
