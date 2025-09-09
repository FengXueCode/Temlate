package cn.zfizwy.killquestionend.utils;


import cn.zfizwy.killquestionend.common.Constant;
import com.alibaba.fastjson.JSON;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class WeChatUtil {

    private static RestOperations restTemplate = new RestTemplate();


    /**
     * 通过微信小程序的code换取用户的OpenID
     *
     * @param code 小程序登录时获取的code
     * @return 用户的OpenID
     */
    public static String getOpenID(String code) {
        // 构建用于获取OpenID的API请求URL
        String url = "https://api.weixin.qq.com/sns/jscode2session?" +
                "appid=" + Constant.APP_ID +
                "&secret=" + Constant.APP_SECRET +
                "&js_code=" + code +
                "&grant_type=authorization_code";
        // 发起HTTP请求获取响应
        String res = restTemplate.getForObject(url, String.class);
        // 解析JSON响应，提取OpenID
        String openId = JSON.parseObject(res).getString("openid");
        return openId;
    }

    /**
     * 获取微信访问令牌（AccessToken）。
     * 该方法通过向微信API发送请求，获取到的AccessToken是进行其他微信接口调用的基础。
     *
     * @return 返回从微信服务器获取到的AccessToken字符串。
     */
    public static String getAccessToken() {
        // 构建请求URL，其中包括grant_type、appid和secret等参数
        String url = "https://api.weixin.qq.com/cgi-bin/token?" +
                "grant_type=client_credential" +
                "&appid=" + Constant.APP_ID +
                "&secret=" + Constant.APP_SECRET;
        // 向指定URL发送GET请求，并返回响应结果
        String res = restTemplate.getForObject(url, String.class);
        // 解析JSON响应，提取AccessToken值
        String token = JSON.parseObject(res).getString("access_token");
        return token;
    }



}
