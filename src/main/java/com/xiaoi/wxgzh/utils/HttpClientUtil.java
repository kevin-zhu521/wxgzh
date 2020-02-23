package com.xiaoi.wxgzh.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
public class HttpClientUtil {

    /**
     * 最大连接数
     */
    private final static int MAX_TOTAL_CONNECTIONS = 128;
    /**
     * 每个路由最大连接数,对于同一个目标机器的最大并发连接只有默认只有2个
     * 哪怕你设置连接池的最大连接数为200，但是实际上还是只有2个连接在工作，
     * 其他剩余的198个连接都在等待，都是为别的目标机器服务的（目标服务器通常指同一台服务器或者同一个域名）
     */
    private final static int MAX_ROUTE_CONNECTIONS = 32; //1000;//100  除以个负载数
    /**
     * 连接超时时间 10s
     */
    private final static int CONNECT_TIMEOUT = 10 * 1000;
    /**
     * 读取超时时间 5s
     */
    private final static int READ_TIMEOUT = 10 * 1000;

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 5.1; rv:13.0) Gecko/20100101 Firefox/13.0.1";
    private static final String CHARSET = "UTF-8";
    private static HttpClient httpClient;

    static {
        configureClient();
    }

    private static void configureClient() {
        //socket配置
        SocketConfig socketConfig = SocketConfig.custom()
                //是否立即发送数据，设置为true会关闭Socket缓冲，默认为false
                .setTcpNoDelay(true)
                //接收数据的等待超时时间，单位ms
                .setSoTimeout(CONNECT_TIMEOUT)
                //关闭Socket时，要么发送完所有数据，要么等待60s后，就关闭连接，此时socket.close()是阻塞的
                .setSoLinger(60)
                //在一个进程关闭Socket后，即使它还没有释放端口，其它进程还可以立即重用端口
                .setSoReuseAddress(true)
                //开启监视TCP连接是否有效
                .setSoKeepAlive(true)
                .build();
        //connection相关配置
        ConnectionConfig connectConfig = ConnectionConfig.custom()
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .setCharset(Consts.UTF_8)
                .build();
        //Request配置
        RequestConfig requestConfig = RequestConfig.custom()
                .setExpectContinueEnabled(true)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(READ_TIMEOUT)
                .setConnectionRequestTimeout(CONNECT_TIMEOUT)
                .build();
        //连接管理器配置
        PoolingHttpClientConnectionManager connectManager = new PoolingHttpClientConnectionManager();
        connectManager.setDefaultConnectionConfig(connectConfig);
        connectManager.setDefaultSocketConfig(socketConfig);
        connectManager.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);
        connectManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);

        httpClient = HttpClients.custom()
                .setConnectionManager(connectManager)
                .setDefaultRequestConfig(requestConfig)
                .build();

    }

    public static byte[] doGet(String url, String params) {
        HttpGet get = new HttpGet(url + (params == null ? "" : params));
        get.setHeader("User-Agent", USER_AGENT);
        try {
            HttpResponse response = httpClient.execute(get);
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                return EntityUtils.toByteArray(response.getEntity());
            } else {
                get.abort();//如果不想获取HTTPClient返回的信息  断开连接
                log.debug("HttpClient doGet statusCode:" + response.getStatusLine().getStatusCode() + " url :" + url);
            }
        } catch (Exception e) {
            log.warn("HttpClient doGet {} => {} ", url, e.getMessage());
            get.abort();//如果不想获取HTTPClient返回的信息  断开连接
        } finally {
            get.releaseConnection();
        }
        return null;
    }

    @SneakyThrows
    public static String doGet(String url, String params, String encode) {
        byte[] is = doGet(url, params);
        if (is != null) {
            return IOUtils.toString(is, encode);
        }
        return null;
    }

    /**
     * List <NameValuePair> nvps = new ArrayList <NameValuePair>();
     * nvps.addParam(new BasicNameValuePair("name", "1"));//名值对
     * nvps.addParam(new BasicNameValuePair("account", "xxxx"));
     */
    public static byte[] doPost(String url, List<NameValuePair> params) {
        HttpPost post = new HttpPost(url);
        post.setHeader("Connection", "close");
        post.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
        try {
            HttpResponse response = httpClient.execute(post);
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                if (response.getEntity() != null) {
                    return EntityUtils.toByteArray(response.getEntity());
                }
            } else {
                log.info("HttpClientUtils statusCode:" + response.getStatusLine().getStatusCode() + " url :" + url);
                post.abort();//如果不想获取HTTPClient返回的信息  断开连接
            }
        } catch (Exception e) {
            log.warn("HttpClient doPost {} => {} ", url, e.getMessage());
            post.abort();//如果不想获取HTTPClient返回的信息  断开连接
        } finally {
            post.releaseConnection();//释放连接
        }
        return null;
    }

    public static String doPost(String url, List<NameValuePair> params, String encode) {
        byte[] is = doPost(url, params);
        if (is != null) {
            try {
                return IOUtils.toString(is, encode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 发送文件，map封装需要传入的参数和文件
     *
     * @usage <pre>Map<String, Object> map = new HashMap<String, Object>(){
     * {
     * put("image", file);
     * put("filename", "iamaimage.jpg");
     * }
     * };
     * HttpClientUtils.doPost(url, map);</pre>
     */
    @SneakyThrows
    public static String doPost(String url, Map<String, Object> map) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof File) {
                builder.addBinaryBody(entry.getKey(), (File) entry.getValue());
            } else {
                builder.addTextBody(entry.getKey(), entry.getValue().toString());
            }
        }
        byte[] is = doPost(url, builder.build());
        if (is != null) {
            return IOUtils.toString(is, CHARSET);
        }
        return "";
    }

    private static byte[] doPost(String url, HttpEntity entity) {
        HttpPost post = new HttpPost(url);
        try {
            post.setEntity(entity);
            HttpResponse response = httpClient.execute(post);

            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                EntityUtils.consume(entity);
                log.debug("MultipartEntity info send ok. ");
                if (response.getEntity() != null) {
                    return EntityUtils.toByteArray(response.getEntity());
                }
            } else {
                log.error(EntityUtils.toString(response.getEntity()));
                post.abort();//如果不想获取HTTPClient返回的信息  断开连接
            }
        } catch (IOException e) {
            log.warn("HttpClient doPost {} => {} ", url, e.getMessage());
            post.abort();//如果不想获取HTTPClient返回的信息  断开连接
        } finally {
            post.releaseConnection();//释放连接
        }
        return null;
    }

    public static String doMapPost(String url, Map<String, Object> maps) {
        //设置参数
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        Iterator iterator = maps.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> elem = (Map.Entry<String, String>) iterator.next();
            list.add(new BasicNameValuePair(elem.getKey(), String.valueOf(elem.getValue())));
        }
        HttpPost post = new HttpPost(url);
        post.setHeader("Connection", "close");
        post.setEntity(new UrlEncodedFormEntity(list, Consts.UTF_8));
        try {
            HttpResponse response = httpClient.execute(post);
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                if (response.getEntity() != null) {
                    return EntityUtils.toString(response.getEntity());
                }
            } else {
                log.info("HttpClientUtils statusCode:" + response.getStatusLine().getStatusCode() + " url :" + url);
                post.abort();//如果不想获取HTTPClient返回的信息  断开连接
            }
        } catch (Exception e) {
            log.warn("HttpClient doPost {} => {} ", url, e.getMessage());
            post.abort();//如果不想获取HTTPClient返回的信息  断开连接
        } finally {
            post.releaseConnection();//释放连接
        }
        return null;
    }

    public static String doJsonPost(String url, String jsonStr) {
        HttpPost post = new HttpPost(url);
        post.setHeader("Connection", "close");
        try {
            StringEntity s = new StringEntity(jsonStr);
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");//发送json数据需要设置contentType
            post.setEntity(s);
            HttpResponse response = httpClient.execute(post);
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                if (response.getEntity() != null) {
                    return EntityUtils.toString(response.getEntity());
                }
            } else {
                log.info("HttpClientUtils statusCode:" + response.getStatusLine().getStatusCode() + " url :" + url);
                post.abort();//如果不想获取HTTPClient返回的信息  断开连接
            }
        } catch (Exception e) {
            log.warn("HttpClient doPost {} => {} ", url, e.getMessage());
            post.abort();//如果不想获取HTTPClient返回的信息  断开连接
        } finally {
            post.releaseConnection();//释放连接
        }
        return null;
    }

    /**
     * 微信公众号上传临时素材
     *
     * @param [url, filePath]
     * @return java.lang.String
     * @author kevin.zhu
     * @date 2020/2/22 20:30
     */
    public static String WxUploadFile(String url, String filePath) throws ClientProtocolException, IOException {
        HttpPost post = new HttpPost(url);
        File file = new File(filePath);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpEntity entity = null;
        HttpResponse response = null;
        String BoundaryStr = "------------7da2e536604c8";
        post.addHeader("Connection", "keep-alive");
        post.addHeader("Accept", "*/*");
        post.addHeader("Content-Type", "multipart/form-data;boundary=" + BoundaryStr);
        post.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
        MultipartEntityBuilder meb = MultipartEntityBuilder.create();
        meb.setBoundary(BoundaryStr).setCharset(Charset.forName("utf-8")).setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        meb.addBinaryBody("media", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
        entity = meb.build();
        post.setEntity(entity);
        response = httpclient.execute(post);
        entity = response.getEntity();
        String result = EntityUtils.toString(entity, "utf-8");
        EntityUtils.consume(entity);// 关闭流
        return result;
    }


    public static HttpClient getHttpClient() {
        return httpClient;
    }
}
