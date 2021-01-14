package com.junling.comunity.controller;

import com.google.code.kaptcha.Producer;
import com.junling.comunity.utility.CommunityUtil;
import com.junling.comunity.utility.KaptchaUtil;
import com.junling.comunity.utility.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

@Controller
public class KaptchaController {
    @Autowired
    private KaptchaUtil kaptchaUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @GetMapping("/kaptcha")
    public void kaptcha(HttpServletResponse response){
        Producer producer = kaptchaUtil.kaptchaProducer();
        String text = producer.createText();

        String label = CommunityUtil.generateUUID();
        String kaptchaKey = RedisKey.getKaptchaKey(label);
        redisTemplate.opsForValue().set(kaptchaKey, text,60, TimeUnit.SECONDS);

        Cookie cookie = new Cookie("label", label);
        cookie.setMaxAge(60);
        cookie.setPath("/");
        response.addCookie(cookie);



        BufferedImage image = producer.createImage(text);
        response.setContentType("image/png");
        try {
            OutputStream io = response.getOutputStream();
            ImageIO.write(image, "png", io);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
