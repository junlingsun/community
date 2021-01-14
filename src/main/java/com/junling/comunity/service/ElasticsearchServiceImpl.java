package com.junling.comunity.service;

import com.alibaba.fastjson.JSONObject;
import com.junling.comunity.dao.elasticsearch.EsPostDao;
import com.junling.comunity.entity.Post;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ElasticsearchServiceImpl implements ElasticsearchService{
    @Autowired
    private EsPostDao esPostDao;

    @Autowired
    private RestHighLevelClient client;

    @Override
    public void save(Post post) {

        esPostDao.save(post);

    }

    @Override
    public void delete(int id) {
        esPostDao.deleteById(id);

    }

    @Override
    public List<Post> search(String keyword, int current, int limit) throws IOException, ParseException {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .sort("type", SortOrder.DESC)
                .sort("score", SortOrder.DESC)
                .sort("createTime", SortOrder.DESC)
                .from(current)
                .size(limit)
                .highlighter(new HighlightBuilder().field("title").preTags("<em>").postTags("</em>").field("content").preTags("<em>").postTags("</em>"));

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("post");
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();

        List<Post> list = new ArrayList<>();
        for (SearchHit hit: hits) {
            String source = hit.getSourceAsString();
            Post post = JSONObject.parseObject(source, Post.class);

            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields.containsKey("title")) {
                post.setTitle(highlightFields.get("title").fragments()[0].toString());
            }

            if(highlightFields.containsKey("content")) {
                post.setContent(highlightFields.get("content").fragments()[0].toString());
            }
            list.add(post);

        }
        return list;
    }

    @Override
    public long totalCount(String keyword) throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.multiMatchQuery(keyword, "title", "content"));
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("post");
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return searchResponse.getHits().getTotalHits().value;
    }
}
