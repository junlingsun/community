package com.junling.comunity.utility;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static String getValue(HttpServletRequest request, String name) {

        Cookie[] cookies = request.getCookies();

        for (Cookie cookie: cookies) {

            if (StringUtils.equals(name, cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;

    }
}
