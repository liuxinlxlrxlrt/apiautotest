package com.api.autotest.util;

import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

public class HttpClientUtil {
    private static Logger logger = Logger.getLogger(HttpClientUtil.class);

    private static final HttpClientBuilder httpClientBuilder = HttpClients.custom();

    {
        //1、绕过不安全的https请求的证书检验
        Registry<ConnectionSocketFactory> registry = null;
        try {
            registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", trustHttpsCertificates())
                    .build();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        //2、创建链接池
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);

        cm.setMaxTotal(50);//连接池最大有50个链接,<=20

        //ip+端口号=一个路由
        cm.setDefaultMaxPerRoute(50);//每个路由默认有多少个链接，<=2

/*
         //连接池的最大连接数
        System.out.println(cm.getMaxTotal());
         //每一个路由的最大连接数
        System.out.println(cm.getDefaultMaxPerRoute());
         //连接池的最大连接数
        PoolStats totalstats = cm.getTotalStats();
        System.out.println(totalstats.getMax());
         //连接池里面有有多少个链接被占用了
        System.out.println(totalstats.getLeased());
         //链接池里面有多少个链接可用
        System.out.println(totalstats.getAvailable());
*/

        httpClientBuilder.setConnectionManager(cm);

        //3、设置默认请求配置
//        RequestConfig requestConfig = RequestConfig.custom()
//                .setConnectTimeout(5000)
//                .setSocketTimeout(3000)
//                .setConnectionRequestTimeout(5000)
//                .build();
//        httpClientBuilder.setDefaultRequestConfig(requestConfig);

        //4、设置默认的一些header
//        List<Header> headers = new ArrayList<>();
//        BasicHeader userAgentHeader = new BasicHeader("User-Agent", "xxx");
//        headers.add(userAgentHeader);
//        httpClientBuilder.setDefaultHeaders(headers);
    }

    //保存sessionId
    public static Map<String, String> cookies = new HashMap<>();

    /**
     * get请求
     *
     * @param url
     * @param params
     * @param headers
     */
    public static String doGet(String url, Map<String, String> params, Map<String, String> headers) throws UnsupportedEncodingException {
        logger.info("接口请求地址：" + url);
        if (params != null) {
            //添加参数
            int mark = 1;
            Set<String> keys = params.keySet();
            for (String key : keys) {
                if (mark == 1) {
                    url += "?" + key + "=" + params.get(key);
                } else {
//                    String encode = URLEncoder.encode(params.get(key), "utf-8");
                    url += "&" + key + "=" + params.get(key);
                }
                mark++;
            }
        }
        HttpGet get = new HttpGet(url);
        //把headers添加到实体中
        if (headers != null) {
            Set<Map.Entry<String, String>> entries = headers.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                get.addHeader(new BasicHeader(entry.getKey(), entry.getValue()));
            }
        }

        //3、设置默认请求配置
        RequestConfig build = RequestConfig.custom()
                //设置连接超时，ms，完成tcp三次握手上限
                .setConnectTimeout(5000)
                //读取超时，ms，表示从请求网址出获取响应数据的时间间隔
                .setSocketTimeout(3000)
                //从连接池获取connection超时时间
                .setConnectionRequestTimeout(5000)
                .build();
        get.setConfig(build);

//        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();

        CloseableHttpResponse response = null;
        String result = "";
        try {
            addCookieInRequestHeaderBeforeRequest(get);
            response = closeableHttpClient.execute(get);
            getAndStoreCookiesFromResponseHeader(response);
            StatusLine statusLine = response.getStatusLine();

            if (HttpStatus.SC_OK == statusLine.getStatusCode()) {
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                logger.info("接口响应数据：" + result);
            } else {
                logger.error("响应失败，响应码：" + statusLine.getStatusCode());
            }
        } catch (IOException e) {
//            logger.error("executeGet error,url:{}", url, e);
            e.printStackTrace();
        } finally {
            HttpClientUtils.closeQuietly(response);
        }
        return result;
    }

    /**
     * \
     * 表单post请求
     *
     * @param url
     * @param params
     * @param headers
     */
    public static String doFormPost(String url, Map<String, String> params, Map<String, String> headers) {
        logger.info("接口请求地址：" + url);
//        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpPost post = new HttpPost(url);

        if (params != null) {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            for (Map.Entry<String, String> param : params.entrySet()) {
                nameValuePairs.add(new BasicNameValuePair(param.getKey(), param.getValue()));
            }
            //把参数集合设置到formEntity
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8);
            post.setEntity(formEntity);
        }

        //把headers添加到实体中
        if (headers != null) {
            Set<Map.Entry<String, String>> entries = headers.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                post.addHeader(new BasicHeader(entry.getKey(), entry.getValue()));
            }
        }

        //3、设置默认请求配置
        RequestConfig build = RequestConfig.custom()
                //设置连接超时，ms，完成tcp三次握手上限
                .setConnectTimeout(5000)
                //读取超时，ms，表示从请求网址出获取响应数据的时间间隔
                .setSocketTimeout(3000)
                //从连接池获取connection超时时间
                .setConnectionRequestTimeout(5000)
                .build();
        post.setConfig(build);

        CloseableHttpResponse response = null;
        String result = "";
        try {
            addCookieInRequestHeaderBeforeRequest(post);
            response = closeableHttpClient.execute(post);

            getAndStoreCookiesFromResponseHeader(response);
            StatusLine statusLine = response.getStatusLine();

            if (HttpStatus.SC_OK == statusLine.getStatusCode()) {
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                logger.info("接口响应数据：" + result);
            } else {
                logger.error("响应失败，响应码：" + statusLine.getStatusCode());
            }
        } catch (IOException e) {
//            logger.error("executeGet error,url:{}", url, e);
            e.printStackTrace();
        } finally {
            HttpClientUtils.closeQuietly(response);
        }
        return result;
    }

    /**
     * json请求post
     *
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public static String doJsonPost(String url, String body, Map<String, String> headers) {

//        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpPost post = new HttpPost(url);

        //设置请求体
        StringEntity jsonEntity = new StringEntity(body, Consts.UTF_8);
        //也需要给Entity设置内容类型
        jsonEntity.setContentType("application/json;charset=utf-8");
        //设置entity编码
        jsonEntity.setContentEncoding(Consts.UTF_8.name());

        post.setEntity(jsonEntity);

        //把headers添加到实体中
        if (headers != null) {
            Set<Map.Entry<String, String>> entries = headers.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                post.addHeader(new BasicHeader(entry.getKey(), entry.getValue()));
            }
        }

        //3、设置默认请求配置
        RequestConfig build = RequestConfig.custom()
                //设置连接超时，ms，完成tcp三次握手上限
                .setConnectTimeout(5000)
                //读取超时，ms，表示从请求网址出获取响应数据的时间间隔
                .setSocketTimeout(3000)
                //从连接池获取connection超时时间
                .setConnectionRequestTimeout(5000)
                .build();
        post.setConfig(build);

        CloseableHttpResponse response = null;

        try {
            addCookieInRequestHeaderBeforeRequest(post);
            response = closeableHttpClient.execute(post);
            getAndStoreCookiesFromResponseHeader(response);

            StatusLine statusLine = response.getStatusLine();

            if (HttpStatus.SC_OK == statusLine.getStatusCode()) {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, StandardCharsets.UTF_8);
            } else {
                logger.error("响应失败，响应码：" + statusLine.getStatusCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
//            logger.error("executeGet error,url:{}", url, e);
        } finally {
            HttpClientUtils.closeQuietly(response);
        }
        return null;
    }

    /**
     * 发送post请求不添加cookie
     *
     * @param url
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String doPostWithNoCookie(String url, Map<String, String> params) throws UnsupportedEncodingException {

//        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpPost post = new HttpPost(url);

        if (params != null) {
            List<BasicNameValuePair> paramters = new ArrayList<>();
            Set<String> keys = params.keySet();
            for (String name : keys) {
                String value = params.get(name);
                paramters.add(new BasicNameValuePair(name, value));
            }
            post.setEntity(new UrlEncodedFormEntity(paramters, "utf-8"));
        }

        post.addHeader("Content-Type", "application/x-www-form-urlencoded");

        HttpResponse response = null;

        try {

            response = closeableHttpClient.execute(post);
            StatusLine statusLine = response.getStatusLine();

            if (HttpStatus.SC_OK == statusLine.getStatusCode()) {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, StandardCharsets.UTF_8);
            } else {
                logger.error("响应失败，响应码：" + statusLine.getStatusCode());
            }
        } catch (IOException e) {
//            logger.error("executeGet error,url:{}", url, e);
            e.printStackTrace();
        } finally {
            HttpClientUtils.closeQuietly(response);
        }
        return null;
    }

    /**
     * 根据type判断发送get请求还是post请求
     *
     * @param url
     * @param type
     * @param params
     * @param headers
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String doService(String url, String type, Map<String, String> params, Map<String, String> headers) throws UnsupportedEncodingException {
        String result = "";
        if ("post".equalsIgnoreCase(type)) {
            result = HttpClientUtil.doFormPost(url, params, headers);
        } else {
            result = HttpClientUtil.doGet(url, params, headers);
        }
        return result;
    }

    /**
     * 判断响应头中是否有JSESSIONID,有就保存到map中
     *
     * @param response
     */
    public static void getAndStoreCookiesFromResponseHeader(CloseableHttpResponse response) {
        //从响应头中取出名字为Set-Cookie的响应头
        Header setCookieHeader = response.getFirstHeader("Set-Cookie");
        //如果不为空
        if (setCookieHeader != null) {
            //取出响应头的值
            String cookiePairsString = setCookieHeader.getValue();
            if (cookiePairsString != null && cookiePairsString.trim().length() > 0) {
                String[] cookiePairs = cookiePairsString.split(";");
                if (cookiePairs != null) {
                    for (String cookiePair : cookiePairs) {
                        //如果包含JSESSIONID，则意味着响应头中里有会话id这个数据
                        if (cookiePair.contains("JSESSIONID")) {
                            //保存到map
                            cookies.put("JSESSIONID", cookiePair);
                        }
                    }
                }
            }
        }
    }

    /**
     * 将Cookie添加到请求头中
     *
     * @param post
     */
    private static void addCookieInRequestHeaderBeforeRequest(HttpRequest request) {
        //判断cookies
        String jsessionIdCookie = cookies.get("JSESSIONID");
        if (jsessionIdCookie != null) {
            request.addHeader("Cookie", jsessionIdCookie);
        }
    }

    //创建支持安全协议的链接工厂
    private ConnectionSocketFactory trustHttpsCertificates() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
        //加载信任的证书
        sslContextBuilder.loadTrustMaterial(null, new TrustStrategy() {
            //判断是否信任url
            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                return true;
            }
        });
        SSLContext sslContext = sslContextBuilder.build();
        SSLConnectionSocketFactory sslConnectionSocketFactory = new
                SSLConnectionSocketFactory(sslContext, new String[]{
                "SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"}
                , null, NoopHostnameVerifier.INSTANCE);
        return sslConnectionSocketFactory;
    }

}
