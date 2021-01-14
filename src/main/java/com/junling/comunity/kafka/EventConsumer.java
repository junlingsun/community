package com.junling.comunity.kafka;


import com.alibaba.fastjson.JSONObject;

import com.junling.comunity.dao.elasticsearch.EsPostDao;
import com.junling.comunity.entity.Event;
import com.junling.comunity.entity.Message;

import com.junling.comunity.entity.Post;
import com.junling.comunity.service.CommentService;
import com.junling.comunity.service.ElasticsearchService;
import com.junling.comunity.service.MessageService;
import com.junling.comunity.service.PostService;
import com.junling.comunity.utility.Constant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements Constant {

    @Autowired
    private MessageService messageService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private ElasticsearchService searchService;

    @KafkaListener(topics = {MESSAGE_TOPIC, LIKE_TOPIC, FOLLOW_TOPIC})
    public void eventListener(ConsumerRecord record){
        if (record == null) {
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
           return;
        }
        Message message = new Message();
        message.setStatus(0);
        message.setCreateTime(new Date());
        message.setConversationId(event.getTopic());
        message.setFromId(SYSTEM_ID);
        message.setToId(event.getEntityUserId());

        //content
        Map<String, Object> content = new HashMap<>();
        content.put("fromUser", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        for (String key: event.getMap().keySet()) {
            content.put(key, event.getMap().get(key));
        }


        message.setContent(JSONObject.toJSONString(content));
        messageService.insertMessage(message);

    }

    @KafkaListener(topics={POST_TOPIC})
    public void eventListener2(ConsumerRecord record) {
        if (record == null) {
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);

        if (event == null) {
            return;
        }

        Post post = postService.findPostById(event.getEntityId());

        searchService.save(post);

    }

    @KafkaListener(topics = DELETE_TOPIC)
    public void deleteListener(ConsumerRecord record) {
        if (record == null) {
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);

        if (event == null) {
            return;
        }
        searchService.delete(event.getEntityId());

    }
}
