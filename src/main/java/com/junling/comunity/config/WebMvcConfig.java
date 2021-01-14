package com.junling.comunity.config;

import com.junling.comunity.interceptor.DataInteceptor;
import com.junling.comunity.interceptor.LoginRequiredInterceptor;
import com.junling.comunity.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private LoginTicketInterceptor interceptor;

    @Autowired
    private DataInteceptor dataInteceptor;

//    @Autowired
//    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor);
        registry.addInterceptor(dataInteceptor);
//        registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/user/settingPage", "/user/passwordUpdate", "/user/headerUpdate");
    }


}
