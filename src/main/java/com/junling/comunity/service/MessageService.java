package com.junling.comunity.service;

import com.junling.comunity.entity.Message;

import java.util.List;

public interface MessageService {

    List<Message> messages(int toId,int offset, int limit);
    int conversationCount(int toId);
    int messageCountPerConversation(int toId,String conversation);
    int unreadCountPerConversation(int toId, String conversation);
    int totalUnreadCount(int toId);

    List<Message> messagesPerConversation(String conversationId, int offset, int limit);
    int totalMessagesPerConversation(String conversationID);
    int updateStatus(String conversationId, int toId, int status);

    int insertMessage(Message message);

    Message lastestMessage(int toId, String topic);
    int unread(int toId, String topic);
    int count(int toId, String topic);

    List<Message> noticeList (int toId, String topic, int offset, int limit);

}
