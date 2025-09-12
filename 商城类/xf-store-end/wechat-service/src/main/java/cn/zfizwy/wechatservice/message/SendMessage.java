package cn.zfizwy.wechatservice.message;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static cn.zfizwy.wechatservice.util.WeChatUtil.getAccessToken;

public class SendMessage {
    public static void sendTemplateMessage(WXMSGVO wxmsgvo) {
        // 从WXMSGVO对象中提取必要的字段
        String openId = wxmsgvo.getOpenId();
        System.out.println("openId = " + openId);
        String templateId = wxmsgvo.getTemplateId();
        String page = wxmsgvo.getPage();
        Map<String, TemplateDataVO> data = wxmsgvo.getData();

        // 构建发送模板消息的API请求URL
        String url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?" +
                "access_token=" + getAccessToken();

        // 使用fastjson将data转换为JSON字符串
        String jsonData = JSON.toJSONString(data);

        // 构建请求体，包括模板ID、接收者OpenID、页面路径和模板数据
        String requestBody = String.format("{"
                + "\"touser\":\"%s\","
                + "\"template_id\":\"%s\","
                + "\"page\":\"%s\","
                + "\"data\":%s,"
                + "\"miniprogram_state\":\"formal\","
                + "\"lang\":\"zh_CN\""
                + "}", openId, templateId, page, jsonData);

        try {
            // 创建URL对象
            URL obj = new URL(url);
            // 打开连接
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            // 设置请求方法为POST
            con.setRequestMethod("POST");
            // 设置请求头
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            // 发送请求体
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 获取响应码
            int responseCode = con.getResponseCode();
            System.out.println("POST Response Code :: " + responseCode);

            // 处理响应
            if (responseCode == HttpURLConnection.HTTP_OK) { // 成功
                System.out.println("Message sent successfully");

                // 读取响应体
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println("Response Body: " + response.toString());
                }
            } else {
                System.out.println("POST request not worked");

                // 读取错误响应体
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println("Error Response Body: " + response.toString());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
