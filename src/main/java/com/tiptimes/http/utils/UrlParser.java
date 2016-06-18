package com.tiptimes.http.utils;


import com.tiptimes.utils.L;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 网络请求参数
 */
public class UrlParser {
    private static final String APP_KEY = "148731093hh56yhdthtg346h34hyebyn6n6w4xg3xhqh64j";
    public static long serverTime; //服务器时间 秒
    public static long localTime; //本地时间 秒
    public static String sid;

    public static String parse(String url, Map<String, String> map) {
        List<String> list = new ArrayList<>();
        String[] params = new String[]{};
        if (map != null) {
            for (String key : map.keySet()) {
                list.add(key);
                list.add(map.get(key));
                L.e("key --> " + key + " val --> " + map.get(key));
            }
            params = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                params[i] = list.get(i);
            }
        }


        return parse(url, params);
    }

    public static String parse(String url, String... params) {

        //校准后的服务器时间
        long realServerTime = ((new Date().getTime() / 1000) - localTime) + serverTime;
        url += url.endsWith("&") ? ("_time=" + realServerTime) : ("&_time=" + realServerTime);
        //用户的sid
        url += sid == null ? "" : ("&_sid=" + sid);

        //get参数
        String getParams = "";
        //post参数
        String postParams = "";
        if (params != null) {
            for (int i = 0; i < params.length; i += 2) {
                postParams += (params[i] + "=" + params[i + 1] + "&");
            }
            getParams = url.split("\\?", 2).length == 2 ? url.split("\\?", 2)[1] : "";
            if (getParams.equals("") && !postParams.equals("")) { //没有get参数
                postParams = postParams.substring(0, postParams.length() - 1); //去掉末尾的&符号
            }
        }
        String param = postParams + getParams;
        String sign = sign(param + UrlParser.APP_KEY);//签名
        url += "&_hash=" + sign;
        return url; //返回新的url

    }

    private static String sign(String data) {
        return SHA1(data);
    }

    private static String SHA1(String decript) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
