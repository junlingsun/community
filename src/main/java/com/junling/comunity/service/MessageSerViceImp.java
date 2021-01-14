package com.junling.comunity.service;

import com.junling.comunity.dao.MessageDao;
import com.junling.comunity.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageSerViceImp implements MessageService{

    @Autowired
    private MessageDao messageDao;

    @Override
    public List<Message> messages(int toId,  int offset, int limit) {
        return messageDao.messages(toId,offset, limit);
    }

    @Override
    public int conversationCount(int toId) {
        return messageDao.conversationCount(toId);
    }

    @Override
    public int messageCountPerConversation(int toId, String conversation) {

        return messageDao.messageCountPerConversation(toId, conversation);
    }

    @Override
    public int unreadCountPerConversation(int toId, String conversation) {
        return messageDao.unreadCountPerConversation(toId, conversation);
    }

    @Override
    public int totalUnreadCount(int toId) {
        return messageDao.totalUnreadCount(toId);
    }

    @Override
    public List<Message> messagesPerConversation(String conversationId, int offset, int limit) {
        return messageDao.messagesPerConversation(conversationId, offset, limit);
    }

    @Override
    public int totalMessagesPerConversation(String conversationId) {
        return messageDao.totalMessagesPerConversation(conversationId);
    }

    @Override
    public int updateStatus(String conversationId, int toId, int status) {
        return messageDao.updateStatus(conversationId, toId, status);
    }

    @Override
    public int insertMessage(Message message) {
        return messageDao.insertMessage(message);
    }

    @Override
    public Message lastestMessage(int toId, String conversationId) {
        return messageDao.LatestMessage(toId, conversationId);
    }

    @Override
    public int unread(int toId, String topic) {

        return messageDao.unread(toId, topic);
    }

    @Override
    public int count(int toId, String topic) {
        return messageDao.count(toId, topic);
    }

    @Override
    public List<Message> noticeList(int toId, String topic, int offset, int limit) {

        return messageDao.noticeList(toId, topic, offset, limit);
    }
}
