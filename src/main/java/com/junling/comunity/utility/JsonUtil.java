package com.junling.comunity.utility;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public class JsonUtil {

    public static String getJsonString(int code, String msg, Map<String, Object> map){
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);

        if (map !=null) {
            for (String key: map.keySet()) {
                json.put(key, map.get(key));
            }
        }

        return json.toJSONString();
    }

    public static String getJsonString(int code, String msg){
        return getJsonString(code, msg, null);
    }

    public static String getJonString(int code) {
        return getJsonString(code, null, null);
    }
}
