package net.mysite.SocialMedia.company.sevice;

import net.mysite.SocialMedia.company.domain.Page;
import net.mysite.SocialMedia.company.dto.PageDto;
import net.mysite.SocialMedia.company.repository.PageRepository;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.err.MissingFieldException;
import net.mysite.SocialMedia.err.PageNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

@Service
public class PageServiceImpl implements PageService{

    @Autowired
    private PageRepository pageRepository;

    @Override
    public Page save(User user, PageDto pageDto) throws MissingFieldException{
        String branch = pageDto.getBranch();
        String email = pageDto.getEmail();
        String description = pageDto.getDesc();
        String phone = pageDto.getPhone();

        if(branch.isEmpty()){ throw new MissingFieldException("Company name cannot be empty."); }
        if(email.isEmpty()){ throw new MissingFieldException("Company email cannot be empty."); }
        if(description.isEmpty()){ throw new MissingFieldException("Company description cannot be empty."); }

        Page newPage = new Page();
        newPage.setCompName(branch);
        newPage.setCompDesc(description);
        newPage.setContactEmail(email);
        if(!phone.isEmpty()) { newPage.setContactPhone(phone); }
        newPage.setCreatedOn(LocalDate.now());
        newPage.setUser(user);
        return pageRepository.save(newPage);
    }

    @Override
    public Set<Page> getMyPages(User user) {
        Set<Page> pageSet = pageRepository.findByUser(user);
        if(pageSet.isEmpty()){ return Collections.emptySet(); }
        return pageSet;
    }

    @Override
    public Page getById(Long pageId) throws PageNotFoundException{
        return pageRepository.findById(pageId)
                .orElseThrow(() -> new PageNotFoundException("Page does not exists."));
    }

    @Override
    public Set<Page> getPagesWithSearchTerm(String searchTerm){
        Set<Page> pageSet = pageRepository.findPagesBySearchTerm(searchTerm);
        if(pageSet.isEmpty()){ return Collections.emptySet(); }
        return pageSet;
    }

    @Override
    public Set<Page> getSetOfPagesNotFollowedByUser(User user) {
        Set<Page> pageSet = pageRepository.findNotFollowedPagesByUser(user);
        if(pageSet.isEmpty()){ return Collections.emptySet(); }
        return pageSet;
    }
}
