package com.xiaoi.wxgzh.wx.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.thoughtworks.xstream.XStream;
import com.xiaoi.wxgzh.Constants;
import com.xiaoi.wxgzh.dto.*;
import com.xiaoi.wxgzh.properties.WxProperties;
import com.xiaoi.wxgzh.utils.EncryptionUtil;
import com.xiaoi.wxgzh.utils.HttpClientUtil;
import com.xiaoi.wxgzh.utils.XmlUtil;
import com.xiaoi.wxgzh.wx.service.RobotService;
import com.xiaoi.wxgzh.wx.service.WxService;
import lombok.SneakyThrows;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author kevin.zhu
 * @date 2020/2/17 11:39
 */
@Service
public class WxServiceImpl implements WxService {

    private TokenInfo tokenInfo;

    @Autowired
    private WxProperties wxProperties;

    @Autowired
    private RobotService robotService;


    @Override
    public String WxCheck(String signature, String timestamp, String nonce, String echostr) {
        //1）将token、timestamp、nonce三个参数进行字典序排序
        // 2）将三个参数字符串拼接成一个字符串进行sha1加密
        // 3）开发者获得加密后的字符串可与signature对比，标识该请求来源于微信,请原样返回echostr参数内容
        String token = wxProperties.getToken();
        String[] param = {token, timestamp, nonce};
        if (EncryptionUtil.checkSign(param, signature)) {
            return echostr;
        }
        return null;
    }

    @Override
    @SneakyThrows
    public String WxMessageProcessing(HttpServletRequest request) {
        InputStream inputStream = request.getInputStream();
        //获取请求xml转成map
        Map<String, String> reqMap = XmlUtil.parseXml(XmlUtil.stream2xml(inputStream));
        //响应处理
        BaseMessage msg = null;
        switch (reqMap.get("MsgType")) {
            case "text":
                msg = dealTextMessage(reqMap);
                break;
            default:
                return "success";
        }
        return XmlUtil.bean2Xml(msg);
    }

    @Override
    public String getToken() {
        if (Objects.isNull(tokenInfo) || tokenInfo.isExpire()) {
            tokenInfo = getTokenToWx();
        }
        return tokenInfo.getToken();
    }

    @Override
    @SneakyThrows
    public String addTemporaryMaterial() {
        String token = getToken();
        String result = HttpClientUtil.WxUploadFile(String.format(wxProperties.getUploadUrl(), token, "image"), "C:\\Users\\User\\Desktop\\222.jpg");
        String thumbMedia = JSONObject.parseObject(result).getString("media_id");
        return thumbMedia;
    }

    @Override
    @SneakyThrows
    public String addPermanentMaterial() {
        String thumbMedia = addTemporaryMaterial();
        String picUrl = getPicUrl();
        String title = new String("这是标题".getBytes("UTF-8"),"ISO-8859-1");
        //todo content为html时，不显示内容，未调试成功
//        String content =Constants.html.replace("https://imgnews.gmw.cn/attachement/jpg/site2/20200223/f44d305ea08b1fbbde3a0d.jpg", picUrl);
        String content ="this is content";
        String param = "{\"articles\":[{\"title\":\"" + title + "\",\"thumb_media_id\":\"" + thumbMedia + "\",\"content\":\"" + content + "\"}]}";
        String token = getToken();
        String result = HttpClientUtil.doJsonPost(String.format(wxProperties.getPermUrl(), token), param);
        String mediaId = JSONObject.parseObject(result).getString("media_id");
        return mediaId;
    }

    @Override
    @SneakyThrows
    public boolean MassSend() {
        String mediaId = addPermanentMaterial();
        String param = "{\"touser\":\"oldDYw4AKHI7s7sp_6otY7rs9mzQ\",\"mpnews\":{\"media_id\":\"" + mediaId + "\"},\"msgtype\":\"mpnews\"}";
        String token = getToken();
        String result = HttpClientUtil.doJsonPost(String.format(wxProperties.getMassUrl(), token), param);
        if (JSONObject.parseObject(result).getInteger("errcode") == 0) {
            return true;
        }
        return false;
    }

    @Override
    @SneakyThrows
    public String getPicUrl() {
        String token = getToken();
        String result = HttpClientUtil.WxUploadFile(String.format(wxProperties.getPicUrl(), token), "C:\\Users\\User\\Desktop\\f44d305ea08b1fbbde3a0d.jpg");
        String url = JSONObject.parseObject(result).getString("url");
        return url;
    }

    @SneakyThrows
    private TokenInfo getTokenToWx() {
        String param = "?grant_type=%s&appid=%s&secret=%s";
        String result = HttpClientUtil.doGet(wxProperties.getTokenUrl(), String.format(param, "client_credential", wxProperties.getAppID(), wxProperties.getAppSecret()), "UTF-8");
        String token = JSONObject.parseObject(result).getString("access_token");
        Long expiresIn = JSONObject.parseObject(result).getLong("expires_in");
        return new TokenInfo(token, expiresIn);
    }


    @SneakyThrows
    private BaseMessage dealTextMessage(Map<String, String> reqMap) {
        String content = reqMap.get("Content");
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        //TODO 在这做的反馈图文测试
        if ("图文".equals(content)) {
            //图文测试
            BaseMessage baseMessage = dealGraphicMessage(reqMap);
            return baseMessage;
        }
        String answer = robotService.robotAnswer(content);
        TextMessage textMessage = new TextMessage(reqMap, answer);
        return textMessage;
    }

    @SneakyThrows
    private BaseMessage dealGraphicMessage(Map<String, String> reqMap) {
        GraphicMessage graphicMessage = new GraphicMessage(reqMap, 1L, "ceshi", "des", "http://a4.att.hudong.com/21/09/01200000026352136359091694357.jpg", "https://www.baidu.com/");
        graphicMessage.setMsgType("news");
        return graphicMessage;
    }

}
