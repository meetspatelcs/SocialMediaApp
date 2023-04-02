package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.domain.Info;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.dto.UserDto;
import net.mysite.SocialMedia.dto.UserEditDto;

import java.util.NoSuchElementException;

public interface InfoService {

    void save(UserDto userDto, User user) throws IllegalArgumentException;

    Info getUserInfoHandler(User user);

    Info getUserInfo(Long userId) throws NoSuchElementException;

    Info getInfoById(Long personalInfoId) throws NoSuchElementException;

    Info updateUserInfo(UserEditDto userEditDto, Info validateInfo, User user);
}