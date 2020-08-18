package cn.com.dyg.work.utils;

import cn.com.dyg.work.http.HttpClientRequest;

public class SnowFlakeUtil {
    public static String getID(String tag){
        HttpClientRequest request = HttpClientRequest.newInstance();
        String id = request.get("http://192.168.20.227:8080/api/snowflake/get/"+tag);
        return id;
    }
}
