package me.redstoner2019.statserver.stats.util;

import me.redstoner2019.util.http.Requests;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;

import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

public class AuthenticationHelper {
    private static final String authIP = "http://158.220.105.209:8080";

    public static AuthenticationResult verifyAuth(String token){
        JSONObject request = new JSONObject();
        request.put("token",token);

        JSONObject data = Requests.request(authIP + "/verifyToken",request);

        if(data.getInt("status") == 0){
            request = new JSONObject();
            request.put("token",token);

            data = Requests.request(authIP + "/tokenInfo",request);

            String username = data.getString("username");
            return new AuthenticationResult("OK",true,200,username);
        } else {
            return new AuthenticationResult("Forbidden: " + data.getString("message"),false,403);
        }
    }

    public static AuthenticationResult verifyAuth(JSONObject request, HttpHeaders headers){
        String token = null;
        if(request.has("token")){
            token = request.getString("token");
        }
        if(headers.containsKey("token")){
            token = headers.get("token").get(0);
        }
        if(headers.containsKey("cookie")){
            Map<String,String> results = getCookies(headers.get("cookie").get(0));
            if(results.containsKey("token")){
                token = results.get("token");
            }
        }
        if(headers.containsKey("Authorization")){
            //Base64.getDecoder().decode(headers.get("Authorization").get(0));
            token = headers.get("Authorization").get(0).replace("Bearer ", "");
        }
        if(token == null) return new AuthenticationResult("Unauthorized",false,401);
        return verifyAuth(token);
    }

    public static Map<String, String> getCookies(String cookieHeader) {
        Map<String, String> cookies = Arrays.stream(cookieHeader.split("; "))
                .map(cookie -> cookie.split("=", 2))  // Split into key and value
                .filter(cookieParts -> cookieParts.length == 2)  // Ensure valid key-value pairs
                .collect(Collectors.toMap(cookieParts -> cookieParts[0], cookieParts -> cookieParts[1]));

        return cookies;
    }
}
