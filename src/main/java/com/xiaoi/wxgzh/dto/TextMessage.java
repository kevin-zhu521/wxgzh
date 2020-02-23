package com.xiaoi.wxgzh.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author kevin.zhu
 * @date 2020/2/18 14:50
 */
@Getter
@Setter
@XStreamAlias("xml")
public class TextMessage extends BaseMessage {

    @XStreamAlias("Content")
    private String content;


    public TextMessage(Map<String, String> map, String content) {
        super(map);
        this.content = content;
    }
}
