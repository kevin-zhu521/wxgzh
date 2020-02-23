package com.xiaoi.wxgzh.wx.service.impl;

import com.xiaoi.wxgzh.WxgzhApplication;
import com.xiaoi.wxgzh.WxgzhApplicationTests;
import com.xiaoi.wxgzh.wx.service.WxService;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kevin.zhu
 * @date 2020/2/21 18:55
 */
class WxServiceImplTest extends WxgzhApplicationTests {

    @Autowired
    private WxService wxService;

    @Test
    void getToken() {
        String token = wxService.getToken();
        System.out.println(token);
    }

    @Test
    void addTemporaryMaterial() {
        String s = wxService.addTemporaryMaterial();
        System.out.println(s);
    }

    @Test
    void massSend() {
        boolean b = wxService.MassSend();
        System.out.println(b);
    }
}