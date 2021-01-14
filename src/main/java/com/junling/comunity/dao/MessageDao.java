package com.junling.comunity.dao;


import com.junling.comunity.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MessageDao {

    List<Message> messages(int toId, int offset, int limit);
    int conversationCount(int toId);
    int messageCountPerConversation(int toId, String conversationId);
    int unreadCountPerConversation(int toId, String conversationId);
    int totalUnreadCount(int toId);

    List<Message> messagesPerConversation(String conversationId, int offset, int limit);
    int totalMessagesPerConversation(String conversationId);
    int  updateStatus(String conversationId, int toId, int status);
    int insertMessage(Message message);

    Message LatestMessage(int toId, String topic);
    int unread(int toId, String topic);
    int count(int toId, String topic);
    List<Message> noticeList(int toId, String topic, int offset, int limit);
}
