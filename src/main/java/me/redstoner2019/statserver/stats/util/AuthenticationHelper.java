package me.redstoner2019.statserver.stats.util;

import me.redstoner2019.util.http.Requests;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;

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
        if(headers.containsKey("Authorization")){
            token = headers.get("Authorization").get(0).replace("Bearer ", "");
        }
        if(token == null) return new AuthenticationResult("Unauthorized",false,401);
        return verifyAuth(token);
    }
}
