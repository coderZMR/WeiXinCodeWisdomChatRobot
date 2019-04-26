package com.github.binarywang.demo.wx.mp.handler;

import com.github.binarywang.demo.wx.mp.chatbot.UnitService;
import com.github.binarywang.demo.wx.mp.builder.TextBuilder;
import com.github.binarywang.demo.wx.mp.coderecommendation.recommendation;
import com.github.binarywang.demo.wx.mp.utils.sendPost;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSession;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Component
public class MsgHandler extends AbstractHandler {
    // CodeWisdom服务功能名与服务的URL
    private static final String CODE_RECOMMENDATION = "CodeRecommendation";
    private static final String CODE_RECOMMENDATION_URL = "http://10.131.253.117:8089/CodeRecommendation/PluginServlet";


    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) {

//        if (!wxMessage.getMsgType().equals(XmlMsgType.EVENT)) {
//            //TODO 可以选择将消息保存到本地
//        }
//
//        //当用户输入关键词如“你好”，“客服”等，并且有客服在线时，把消息转发给在线客服
//        try {
//            if (StringUtils.startsWithAny(wxMessage.getContent(), "你好", "客服")
//                && weixinService.getKefuService().kfOnlineList()
//                .getKfOnlineList().size() > 0) {
//                return WxMpXmlOutMessage.TRANSFER_CUSTOMER_SERVICE()
//                    .fromUser(wxMessage.getToUser())
//                    .toUser(wxMessage.getFromUser()).build();
//            }
//        } catch (WxErrorException e) {
//            e.printStackTrace();
//        }

        //TODO 组装回复消息
        // 后端回复内容
        String content = "聊天机器人无法理解您的输入";
        // 获得用户会话信息，判断用户在上一轮的会话意图，用户在上一轮会话的意图已存入session
        WxSession session = sessionManager.getSession(wxMessage.getFromUser());
        // 判断是否需要代码推荐
        // 此时应在第二轮对话
        boolean flag = true;
        if (session.getAttribute(CODE_RECOMMENDATION) == null) {
            flag = false;
        }
        if (flag && Boolean.parseBoolean(session.getAttribute(CODE_RECOMMENDATION).toString())) {
            try {
                String[] resultArray = new String[10];
                resultArray = recommendation.doRecommendation(CODE_RECOMMENDATION_URL, wxMessage.getContent());
                StringBuilder stringBuilder = new StringBuilder();
                int topNum = 1;
                for(String res : resultArray) {
                    stringBuilder.append("top");
                    stringBuilder.append(topNum);
                    stringBuilder.append(":");
                    stringBuilder.append(res);
                    stringBuilder.append("\r\n");
                    stringBuilder.append("\r\n");
                    topNum++;
                }
                content = stringBuilder.toString();
                session.setAttribute(CODE_RECOMMENDATION, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 此时应在第一轮对话
        else {
            // 指定CodeWisdom服务
            if (StringUtils.contains(wxMessage.getContent(), "推荐") || StringUtils.contains(wxMessage.getContent(), "补全")) {
                session.setAttribute(CODE_RECOMMENDATION, true);
                content = "请输入需要推荐或补全的代码，需要推荐或补全代码的位置请用$hole$标识出";
            }
            else {
                // 没有指定的CodeWisdom服务要求则交由Baidu UNIT2.0 ChatBot处理
                Random r = new Random();
                int rInt = r.nextInt(100);
                content = UnitService.utterance(rInt, "", wxMessage.getFromUser(), wxMessage.getContent());
            }
        }
        return new TextBuilder().build(content, wxMessage, weixinService);
    }

}
