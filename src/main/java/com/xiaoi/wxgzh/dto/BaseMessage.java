package com.xiaoi.wxgzh.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * @author kevin.zhu
 * @date 2020/2/18 14:44
 */
@Getter
@Setter
@NoArgsConstructor
public class BaseMessage {
    @XStreamAlias("ToUserName")
    private String toUserName;

    @XStreamAlias("FromUserName")
    private String fromUserName;

    @XStreamAlias("CreateTime")
    private Long createTime;

    @XStreamAlias("MsgType")
    private String msgType;

    public BaseMessage(Map<String, String> map) {
        this.toUserName = map.getOrDefault("FromUserName", "");
        this.fromUserName = map.getOrDefault("ToUserName", "");
        this.msgType = map.getOrDefault("MsgType", "");
        this.createTime = System.currentTimeMillis() / 1000;
    }
}
