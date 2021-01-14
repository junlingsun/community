package com.junling.comunity.service;

import com.junling.comunity.entity.LoginTicket;
import com.junling.comunity.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;


public interface UserService /*extends UserDetailsService*/ {

    User findUserById(int id);
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    Map<String, Object> saveUser(User user);
    int accountActivation(String code, int id);
    Map<String, Object> login(String username, String password, int expired);
    void logout(int status, String ticket);
    Map<String, Object> passwordUpdate(User user, String password, String newPAssword);
    void updateHeaderUrl(User user, String headerUrl);
    LoginTicket findLoginTicket (String ticket);

    Collection<? extends GrantedAuthority> getAuthorities(int userId);

}
