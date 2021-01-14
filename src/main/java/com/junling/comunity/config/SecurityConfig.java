package com.junling.comunity.config;

import com.junling.comunity.entity.User;
import com.junling.comunity.service.UserService;
import com.junling.comunity.utility.CommunityUtil;
import com.junling.comunity.utility.Constant;
import com.junling.comunity.utility.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements Constant {

    @Autowired
    private UserService userService;

    //ignore static resource
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resource/**");
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {



        //logout
        http.logout().logoutUrl("/logoutSecurity");


        http.authorizeRequests()
                .antMatchers(
                        "/user/settingPage",
                        "/user/headerUpdate",
                        "/comment/add",
                        "/like",
                        "/follow",
                        "/followee/**",
                        "/follower/**",
                        "/messagePage",
                        "/noticePage",
                        "/message/**",
                        "/post/detail/**",
                        "/post/comment/**"
                        )
                .hasAnyAuthority(AUTHORITY_USER, AUTHORITY_MODERATOR, AUTHORITY_ADMIN)
                .antMatchers(
                        "/post/top/**",
                        "/post/wonderful/**"
                )
                .hasAnyAuthority(AUTHORITY_MODERATOR, AUTHORITY_ADMIN)
                .antMatchers(

//                        "/post/delete/**",
//                        "/dataPage",
//                        "/data/**"
                )
                .hasAnyAuthority(AUTHORITY_ADMIN)
                .anyRequest().permitAll();



        http.exceptionHandling()
                //not login handling
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if (StringUtils.equals(xRequestedWith, "XMLHTTPRequest")) {
                            response.setContentType("application/plain; charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(JsonUtil.getJsonString(403, "not login"));
                        }else {
                            response.sendRedirect(request.getContextPath() + "/loginPage");
                        }

                    }
                })
                //no authority handling
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if (StringUtils.equals(xRequestedWith, "XMLHTTPRequest")) {
                            response.setContentType("application/plain; charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(JsonUtil.getJsonString(403, "you have not authority to access the page"));
                        }else {
                            response.sendRedirect(request.getContextPath() + "/index");

                        }
                    }
                });
    }




    }

