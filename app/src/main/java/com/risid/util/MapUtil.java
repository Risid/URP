package com.risid.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Risid on 2017/3/23.
 */

public class MapUtil {
    public static Map<String, String> LoginMap(String id, String pwd, String yzm){
        Map<String, String> loginMap = new HashMap<>();
        loginMap.put("zjh", id);
        loginMap.put("mm", pwd);
        loginMap.put("v_yzm", yzm);
        return loginMap;
    }
    public static Map<String, String> LoginMap(String id, String pwd){
        Map<String, String> loginMap = new HashMap<>();
        loginMap.put("zjh", id);
        loginMap.put("mm", pwd);
        return loginMap;
    }
}
