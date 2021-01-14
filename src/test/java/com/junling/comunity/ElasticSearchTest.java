package com.junling.comunity;


import com.junling.comunity.dao.elasticsearch.EsPostDao;
import com.junling.comunity.entity.Post;
import com.junling.comunity.service.PostService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;

import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.elasticsearch.core.*;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticSearchTest {
    @Autowired
    private PostService postService;
    @Autowired
    private EsPostDao esPostDao;
    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ElasticsearchOperations operations;

    @Autowired
    private ElasticsearchRestTemplate template;

    @Test
    public void saveTest(){
        System.out.println("client " + client);
        esPostDao.save(postService.findPostById(109));
        esPostDao.save(postService.findPostById(110));



    }

    @Test
    public void deleteTest(){
        esPostDao.deleteAll();
    }

    @Test
    public void saveAll(){

        esPostDao.saveAll(postService.posts(101, 100, 0));
        esPostDao.saveAll(postService.posts(102, 100, 0));
        esPostDao.saveAll(postService.posts(103, 100, 0));
        esPostDao.saveAll(postService.posts(111, 100, 0));
        esPostDao.saveAll(postService.posts(112, 100, 0));
        esPostDao.saveAll(postService.posts(131, 100, 0));
        esPostDao.saveAll(postService.posts(132, 100, 0));
        esPostDao.saveAll(postService.posts(133, 100, 0));
        esPostDao.saveAll(postService.posts(134, 100, 0));
    }

    @Test
    public void search() throws IOException, ParseException {

        SearchRequest request = new SearchRequest();
        request.indices("post");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("春天", "title", "content"))
                .from(0)
                .size(10)
                .sort("type", SortOrder.DESC)
                .sort("score", SortOrder.DESC)
                .sort("createTime", SortOrder.DESC)
                .highlighter(new HighlightBuilder().field("title").preTags("<em>").postTags("</em>"));
        request.source(searchSourceBuilder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        System.out.println("total hits " + response.getHits().getTotalHits().value);


        SearchHits hits = response.getHits();
        List<Post> posts = new ArrayList<>();
        for (SearchHit hit: hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            Post post = new Post();
            post.setId((Integer)map.get("id"));
            post.setScore((Double)map.get("score"));
            post.setCommentCount((Integer)map.get("commentCount"));
            post.setStatus((Integer)map.get("status"));
            post.setCreateTime((new SimpleDateFormat("yyyy-MM-dd")).parse(map.get("createTime").toString()));

            post.setType((Integer)map.get("type"));
            post.setContent((String)map.get("content"));
            post.setTitle((String)map.get("title"));
            post.setUserId(Integer.parseInt(map.get("userId").toString()));

            posts.add(post);
            System.out.println(post);
        }
        System.out.println("size " + posts.size());
    }





}
