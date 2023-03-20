package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.domain.Info;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.dto.UserDto;
import net.mysite.SocialMedia.dto.UserEditDto;
import net.mysite.SocialMedia.errors.UserUpdateException;
import net.mysite.SocialMedia.repository.InfoRepository;
import net.mysite.SocialMedia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class InfoService {

    @Autowired
    private InfoRepository infoRepository;

    @Autowired
    private UserRepository userRepository;

    public void save(UserDto userDto, User user){

        if(userDto == null || user == null){
            throw new IllegalArgumentException("userDto or user cannot be null");
        }

        Info newInfo = new Info();
        newInfo.setName(userDto.getFirstname() + " " + userDto.getLastname());
        newInfo.setStartDate(LocalDateTime.now());
        newInfo.setCountry(userDto.getCountry());
        LocalDate date = convertDate(userDto.getDob());
        newInfo.setAge(getAgeFromDate(date));
        newInfo.setState(userDto.getMyState());
        newInfo.setUser(user);
        infoRepository.save(newInfo);
    }
    public Info getUserInfoHandler(User user){
        Long id = user.getId();
        return getUserInfo(id);
    }

    public Info getUserInfo(Long userId){
        return infoRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User with ID " + userId + " not found"));
    }
    public Info getInfoById(Long personalInfoId) {
        return infoRepository.findById(personalInfoId).orElseThrow(() -> new NoSuchElementException("Info with ID " + personalInfoId + " not found."));
    }
    public Info updateUserInfo(UserEditDto userEditDto, Info validateInfo, User user) {
        try {
            String firstname = userEditDto.getUser().getFirstname();
            String lastname = userEditDto.getUser().getLastname();
            user.setFirstname(firstname);
            user.setLastname(lastname);
            userRepository.save(user);

            validateInfo.setName(firstname + " " + lastname);
            validateInfo.setMyBio(userEditDto.getBio());
            validateInfo.setState(userEditDto.getState());
            validateInfo.setCountry(userEditDto.getCountry());
            validateInfo.setEmail(userEditDto.getEmail());
            validateInfo.setPhone(userEditDto.getPhone());

            return infoRepository.save(validateInfo);
        }
        catch (Exception e){
            throw new UserUpdateException("Error updating user's info.");
        }
    }
    private LocalDate convertDate(String date){
        return LocalDate.parse(date);
    }
    private int getAgeFromDate(LocalDate date){
        if(date != null){
            return Period.between(date, LocalDate.now()).getYears();
        }
        return 0;
    }
}
