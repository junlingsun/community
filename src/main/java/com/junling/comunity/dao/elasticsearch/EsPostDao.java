package com.junling.comunity.dao.elasticsearch;

import com.junling.comunity.entity.Post;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsPostDao extends ElasticsearchRepository <Post, Integer> {
}
