package com.xiaoi.wxgzh.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author kevin.zhu
 * @date 2020/2/15 20:48
 */
@Component
@ConfigurationProperties(prefix = "wechat.param")
@Getter
@Setter
public class WxProperties {

    private String token;

    private String key;

    private String url;

    private String appID;

    private String appSecret;

    private String tokenUrl;

    private String uploadUrl;

    private String massUrl;

    private String permUrl;

    private String picUrl;


}
