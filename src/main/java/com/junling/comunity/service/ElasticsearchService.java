package com.junling.comunity.service;

import com.junling.comunity.entity.Post;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface ElasticsearchService {

    public void save (Post post);
    public void delete (int id);
    public long totalCount(String keyword) throws IOException;
    public List<Post> search (String keywords, int offset, int limit) throws IOException, ParseException;
}
