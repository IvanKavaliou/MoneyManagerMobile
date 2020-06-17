package kavaliou.ivan.net.moneymanagermobile.utils;

import android.util.Base64;

import java.util.HashMap;
import java.util.Map;

import kavaliou.ivan.net.moneymanagermobile.model.User;

public class AuthUtils {


    public static Map<String, String> authUser(User user){
        HashMap<String, String> params = new HashMap<String, String>();
        String creds = String.format("%s:%s",user.getEmail(),user.getPassword());
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        params.put("Authorization", auth);
        return params;
    }
}
