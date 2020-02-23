package com.xiaoi.wxgzh.utils;

import com.thoughtworks.xstream.XStream;
import com.xiaoi.wxgzh.dto.Article;
import com.xiaoi.wxgzh.dto.BaseMessage;
import com.xiaoi.wxgzh.dto.GraphicMessage;
import com.xiaoi.wxgzh.dto.TextMessage;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kevin.zhu
 * @date 2020/2/17 18:57
 */
public class XmlUtil {

    /**
     * 解析xml成map
     *
     * @param [xml]
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @author kevin.zhu
     * @date 2020/2/18 15:27
     */
    public static Map<String, String> parseXml(String xml) throws DocumentException {
        Map map = new HashMap();
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new ByteArrayInputStream(xml.getBytes("UTF-8")));//xml串第一行不能有空格，否则报错
            Element root = document.getRootElement();//得到xml文档根节点元素，即最上层的"<xml>"
            elementTomap(root, map);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> elementTomap(Element outele, Map<String, String> outmap) {
        List<Element> list = outele.elements();
        for (Element element : list) {
            String key = element.getName();
            String value = element.getText();
            outmap.put(key, value);
        }
        return outmap;
    }

    public static String stream2xml(InputStream inputStream) {
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            String str = result.toString(StandardCharsets.UTF_8.name());
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * bean解析成xml
     *
     * @param [msg]
     * @return java.lang.String
     * @author kevin.zhu
     * @date 2020/2/18 15:27
     */
    public static String bean2Xml(BaseMessage msg) {
        if (msg == null) {
            return null;
        }
        XStream xStream = new XStream();
        xStream.processAnnotations(new Class[]{TextMessage.class,GraphicMessage.class,Article.class});
        String xml = xStream.toXML(msg);
        return xml;
    }


}
