package com.github.binarywang.demo.wx.mp.coderecommendation;

import com.github.binarywang.demo.wx.mp.utils.sendPost;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class recommendation {
    /**
     * 调用代码推荐服务
     * @param url   推荐服务的url
     * @param code  用户输入的code片段
     * @return      推荐服务推荐的top10结果数组
     * @throws IOException
     */
    public static String[] doRecommendation(String url, String code) throws IOException {
        String[] resultArray = new String[10];
        String responseJson = sendPost.sendPost(url, code);
        JSONArray temp = new JSONArray(responseJson);
        JSONArray codeResult = (JSONArray) temp.get(0);
        for(int i = 0;i<codeResult.length();i++)
        {
            System.out.println(codeResult.get(i));
            JSONObject object = (JSONObject)codeResult.get(i);
            resultArray[i] = object.getString("statement");
        }
        return resultArray;
    }
}
