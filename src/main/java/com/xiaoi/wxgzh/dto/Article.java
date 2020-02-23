package com.xiaoi.wxgzh.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author kevin.zhu
 * @date 2020/2/18 14:56
 */
@Getter
@Setter
@AllArgsConstructor
@XStreamAlias("item")
public class Article {

    @XStreamAlias("Title")
    private String title;
    @XStreamAlias("Description")
    private String description;
    @XStreamAlias("PicUrl")
    private String picUrl;
    @XStreamAlias("Url")
    private String url;


}
