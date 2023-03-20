package net.mysite.SocialMedia.company.sevice;

import net.mysite.SocialMedia.company.domain.Page;
import net.mysite.SocialMedia.company.dto.PageDto;
import net.mysite.SocialMedia.company.repository.PageRepository;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.errors.InvalidInputException;
import net.mysite.SocialMedia.errors.NotFoundException;
import net.mysite.SocialMedia.errors.PageNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Service
public class PageService {

    @Autowired
    private PageRepository pageRepository;

    public Page save(User user, PageDto pageDto) {
        if(pageDto.getBranch().isEmpty()){
            throw new InvalidInputException("Company name cannot be empty.");
        }
        if(pageDto.getEmail().isEmpty()){
            throw new InvalidInputException("Company email cannot be empty.");
        }
        if(pageDto.getDesc().isEmpty()){
            throw new InvalidInputException("Company description cannot be empty.");
        }

        Page newPage = new Page();
        newPage.setCompName(pageDto.getBranch());
        newPage.setCompDesc(pageDto.getDesc());
        newPage.setContactEmail(pageDto.getEmail());
        newPage.setContactPhone(pageDto.getPhone());
        newPage.setCreatedOn(LocalDate.now());
        newPage.setUser(user);

        return pageRepository.save(newPage);
    }

    public Set<Page> getMyPages(User user) {
        Set<Page> pageSet = pageRepository.findByUser(user);
        if(pageSet.isEmpty()){
            throw new PageNotFoundException("Could not find any pages created by user: " + user.getFirstname() + user.getLastname());
        }
        return pageSet;
    }

    public Page getById(Long pageId) {
        Page currPage = pageRepository.findById(pageId).orElseThrow(() -> new PageNotFoundException("Page not found with ID: " + pageId));
        return currPage;
    }

    public Set<Page> getPagesWithSearchTerm(String searchTerm){
        Set<Page> pageSet = pageRepository.findPagesBySearchTerm(searchTerm);
        if(pageSet.isEmpty()){
            throw new NotFoundException("No page found with search term: " + searchTerm);
        }
        return pageSet;
    }

    public Set<Page> getSetOfPagesNotFollowedByUser(User user) {
        Set<Page> pageSet = pageRepository.findNotFollowedPagesByUser(user);
        if(pageSet.isEmpty()){
            throw new NullPointerException("There are no new pages to follow.");
        }
        return pageSet;
    }
}
