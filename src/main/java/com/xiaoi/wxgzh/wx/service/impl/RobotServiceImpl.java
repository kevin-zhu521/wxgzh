package com.xiaoi.wxgzh.wx.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaoi.wxgzh.properties.WxProperties;
import com.xiaoi.wxgzh.wx.service.RobotService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * @author kevin.zhu
 * @date 2020/2/17 20:59
 */
@Service
public class RobotServiceImpl implements RobotService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WxProperties wxProperties;

    @Override
    @SneakyThrows
    public String robotAnswer(String question) {
        String url = wxProperties.getUrl() + "?info=" + question + "&key=" + wxProperties.getKey() + "";
        String result = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject resultObject = jsonObject.getJSONObject("result");
        if (resultObject.getInteger("code") == 100000) {
            return resultObject.getString("text");
        }
        return null;
    }
}
