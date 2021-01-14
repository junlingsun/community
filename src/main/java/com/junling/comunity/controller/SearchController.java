package com.junling.comunity.controller;


import com.junling.comunity.entity.Post;
import com.junling.comunity.service.*;
import com.junling.comunity.utility.Constant;
import com.junling.comunity.utility.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements Constant {

    @Autowired
    private ElasticsearchService searchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;




    @GetMapping("/search")
    public String search(String keyword, PageUtil page, Model model) throws IOException, ParseException {

        long totalCount = searchService.totalCount(keyword);
        page.setTotalRows((int)totalCount);
        page.setPath("/search?keyword=" + keyword);
        page.setRowsPerPage(10);


        List<Post> posts = searchService.search(keyword, page.getOffset(), page.getRowsPerPage());
        List<Map<String, Object>> list = new ArrayList<>();
        for (Post post: posts) {
            Map<String, Object> map = new HashMap<>();
            map.put("post", post);
            map.put("user", userService.findUserById(post.getUserId()));
            map.put("like", likeService.likeCount(POST_TYPE, post.getId()));
            map.put("reply", post.getCommentCount());
            list.add(map);
        }

        model.addAttribute("list", list);
        return "site/search";
    }
}
