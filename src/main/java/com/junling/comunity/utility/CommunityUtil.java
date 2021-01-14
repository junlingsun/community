package com.junling.comunity.utility;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.UUID;

@Component
public class CommunityUtil {

    public static String generateUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String generatePassword(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }

        return DigestUtils.md5DigestAsHex(s.getBytes());
    }
}
