package com.junling.comunity.controller;


import com.junling.comunity.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/data")
public class DataController {

    @Autowired
    private DataService dataService;

    @PostMapping("/uv")
    public String uv(@DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate, Model model) {

        long uvCount = dataService.calculateUV(startDate, endDate);
        model.addAttribute("uvCount", uvCount);
        return "site/admin/data";
    }

    @PostMapping("/dau")
    public String dau(@DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate, Model model) {
        long dauCount = dataService.calculateDAU(startDate, endDate);
        model.addAttribute("dauCount", dauCount);
       return "site/admin/data";
    }
}
