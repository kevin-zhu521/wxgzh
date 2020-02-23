package com.xiaoi.wxgzh.wx.service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author kevin.zhu
 * @date 2020/2/17 11:37
 */
public interface WxService {

    /**
     * 微信校验
     *
     * @return
     */
    public String WxCheck(String signature, String timestamp, String nonce, String echostr);

    /**
     * 消息处理
     *
     * @param [request]
     * @return java.lang.String
     * @author kevin.zhu
     * @date 2020/2/17 18:48
     */
    String WxMessageProcessing(HttpServletRequest request);

    /**
     * 获取token
     *
     * @param []
     * @return java.lang.String
     * @author kevin.zhu
     * @date 2020/2/21 18:34
     */
    String getToken();

    /**
     * 新增临时素材
     *
     * @param []
     * @return java.lang.String
     * @author kevin.zhu
     * @date 2020/2/22 20:33
     */
    String addTemporaryMaterial();

    /**
     * 新增永久素材
     *
     * @param []
     * @return java.lang.String
     * @author kevin.zhu
     * @date 2020/2/22 21:04
     */
    String addPermanentMaterial();

    /**
     * 群发
     *
     * @param []
     * @return java.lang.Boolean
     * @author kevin.zhu
     * @date 2020/2/22 20:51
     */
    boolean MassSend();

    /**
     * 上传图文消息内的图片获取URL
     *
     * @param []
     * @return java.lang.String
     * @author kevin.zhu
     * @date 2020/2/23 19:19
     */
    String getPicUrl();

}
