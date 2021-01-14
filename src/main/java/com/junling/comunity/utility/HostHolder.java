package com.junling.comunity.utility;

import com.junling.comunity.entity.User;
import org.springframework.stereotype.Component;

@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUsers(User user) {
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void remove(){
       users.remove();
    }


}
