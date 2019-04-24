package com.github.binarywang.demo.wx.mp.Bot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/*
 * unit对话服务
 * accessToken的生存期只有30天
 */
public class UnitService {
    /**
     * 处理Baidu UNIT返回的JSON格式的response
     * @param response      Baidu UNIT返回的JSON格式的response
     * @return              将要传到微信公众平台的回答文本
     */
    private static String doJson(String response) {
        JSONObject jObject = new JSONObject(response);
        JSONObject result = jObject.getJSONObject("result");
        JSONArray response_list = result.getJSONArray("response_list");
        JSONObject firstResponse = response_list.getJSONObject(0);
        JSONArray action_list = firstResponse.getJSONArray("action_list");
        JSONObject firstAction = action_list.getJSONObject(0);
        return firstAction.getString("say");
    }
    /**
     * 微信公众号后端与Baidu UNIT服务交互函数
     * UNIT机器人对话API文档：https://ai.baidu.com/docs#/UNIT-v2-service-API/top
     * @param random        用于构造log_id的随机数
     * @param session_id    保存机器人的历史会话信息session的ID
     * @param user_id       与机器人对话的用户id
     * @param query         用户输入的信息
     * @return              将要传到微信公众平台的回答文本
     */
    public static String utterance(int random, String session_id, String user_id, String query) {
        // 请求URL
        String talkUrl = "https://aip.baidubce.com/rpc/2.0/unit/service/chat";
        // log_id生成
        Date t = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String log_id = df.format(t) + " " + random;
        try {
            // 请求参数
            String params = "{" +
                            "\"log_id\":" + "\"" + log_id + "\"," +
                            "\"version\":\"2.0\"," +
                            "\"service_id\":\"S17028\"," +
                            "\"session_id\":" + "\"" + session_id + "\"," +
                            "\"request\":{\"query\":" + "\"" + query + "\"" + "," + "\"user_id\":" + "\"" + user_id + "\"}" +
                            "}";
            String accessToken = "24.3f43019a6c3c38388331252a1c13a2f4.2592000.1558677897.282335-16098382";
            String response = HttpUtil.post(talkUrl, accessToken, "application/json", params);
            return doJson(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 单元测试
     * @param args
     */
    public static void main(String[] args) {
        Random r = new Random();
        int rInt = r.nextInt();
        System.out.println(utterance(rInt, "", "test", "你好"));
    }
}
