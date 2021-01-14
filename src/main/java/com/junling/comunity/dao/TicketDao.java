package com.junling.comunity.dao;

import com.junling.comunity.entity.LoginTicket;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface TicketDao {

    int saveTicket(LoginTicket ticket);

    int updateTicketStatus(int status, String ticket);

    LoginTicket findByTicket(String ticket);

}
