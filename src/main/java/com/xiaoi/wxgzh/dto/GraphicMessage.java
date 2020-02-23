package com.xiaoi.wxgzh.dto;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author kevin.zhu
 * @date 2020/2/18 14:54
 */
@Getter
@Setter
@XStreamAlias("xml")
public class GraphicMessage extends BaseMessage {

    @XStreamAlias("ArticleCount")
    private Long articleCount;

    @XStreamAlias("Articles")
    private List<Article> articles;

    public GraphicMessage(Map<String, String> map, Long articleCount, String title, String description, String picUrl, String url) {
        super(map);
        this.articleCount = articleCount;
        Article article = new Article(title, description, picUrl, url);
        List<Article> articleList = new ArrayList<Article>();
        articleList.add(article);
        this.articles = articleList;
    }

}
