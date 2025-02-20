package me.redstoner2019.statserver.stats.util;

import org.json.JSONObject;
import org.springframework.http.HttpHeaders;

public class HeadersToJson {
    public static JSONObject headersToJson(HttpHeaders headers) {
        JSONObject headersJson = new JSONObject();

        for(String s : headers.keySet()){
            headersJson.put(s,headers.get(s).get(0));
        }

        return headersJson;
    }
}
