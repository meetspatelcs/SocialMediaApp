package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.domain.Info;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.dto.UserDto;
import net.mysite.SocialMedia.dto.UserEditDto;
import net.mysite.SocialMedia.repository.InfoRepository;
import net.mysite.SocialMedia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.NoSuchElementException;

@Service
public class InfoServiceImpl implements InfoService{

    @Autowired
    private InfoRepository infoRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void save(UserDto userDto, User user) throws IllegalArgumentException{

        if(user == null){ throw new IllegalArgumentException("User cannot be null"); }
        if(userDto == null){ throw new IllegalArgumentException("UserDto cannot be null"); }

        String first = userDto.getFirstname();
        String last = userDto.getLastname();
        String date = userDto.getDob();
        String country = userDto.getCountry();
        String myState = userDto.getMyState();

        if(first.isEmpty()){ throw new IllegalArgumentException("First name cannot be null or empty."); }
        if(last.isEmpty()){ throw new IllegalArgumentException("Last name cannot be null or empty."); }
        if(date.isEmpty()){ throw new IllegalArgumentException("Date of Birth cannot be null or empty."); }
        if(country.isEmpty()){ throw new IllegalArgumentException("Country cannot be null or empty."); }
        if(myState.isEmpty()){ throw new IllegalArgumentException("State cannot be null or empty."); }

        Info newInfo = new Info();
        newInfo.setName(first + " " + last);
        newInfo.setStartDate(LocalDateTime.now());
        newInfo.setCountry(country);
        LocalDate dob = convertDate(date);
        newInfo.setAge(getAgeFromDate(dob));
        newInfo.setState(myState);
        newInfo.setUser(user);
        infoRepository.save(newInfo);
    }

    @Override
    public Info getUserInfoHandler(User user){
        Long id = user.getId();
        return getUserInfo(id);
    }

    @Override
    public Info getUserInfo(Long userId) throws NoSuchElementException{
        return infoRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Failed to find information about user."));
    }

    @Override
    public Info getInfoById(Long personalInfoId) throws NoSuchElementException {
        return infoRepository.findById(personalInfoId)
                .orElseThrow(() -> new NoSuchElementException("Failed to find information about user."));
    }

    @Override
    public Info updateUserInfo(UserEditDto userEditDto, Info validateInfo, User user) {

        String first = userEditDto.getUser().getFirstname();
        String last = userEditDto.getUser().getLastname();

        if(!first.isEmpty() && first != null){ user.setFirstname(first); }
        if(!last.isEmpty() && first != null){ user.setLastname(last); }
        userRepository.save(user);

        validateInfo.setName(first + " " + last);
        validateInfo.setMyBio(userEditDto.getBio());
        validateInfo.setState(userEditDto.getState());
        validateInfo.setCountry(userEditDto.getCountry());
        validateInfo.setEmail(userEditDto.getEmail());
        validateInfo.setPhone(userEditDto.getPhone());

        return infoRepository.save(validateInfo);
    }

    private LocalDate convertDate(String date){
        return LocalDate.parse(date);
    }

    private int getAgeFromDate(LocalDate date){
        if(date != null){ return Period.between(date, LocalDate.now()).getYears(); }
        return 0;
    }
}
