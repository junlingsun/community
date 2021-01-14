package com.junling.comunity.dao;

import com.junling.comunity.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserDao {

    User findUserById(int id);
    User findUserByUsername(String username);
    User findUserByEmail(String email);

    boolean saveUser(User user);
    boolean updateUserStatus(int id, int status);
    boolean updatePassword(int id, String password);
    boolean updateHeaderUrl(int id, String headerUrl);
}
