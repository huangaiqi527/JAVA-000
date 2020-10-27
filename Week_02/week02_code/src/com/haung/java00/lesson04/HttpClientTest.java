package com.haung.java00.lesson04;


import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.omg.CORBA.NameValuePairHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpClientTest {
    public static void main(String[] args) throws IOException {
        HttpClientTest test = new HttpClientTest();
        System.out.println("send Get Request");
        test.sendGet("http://localhost:8801");
        System.out.println("send Post Request");
        test.sendPost("http://localhost:8801");
    }

    public void sendGet(String url) throws IOException {
        try(CloseableHttpClient  httpclient = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(url);
            CloseableHttpResponse  httpResponse = httpclient.execute(get);
            StatusLine statusLine = httpResponse.getStatusLine();
            System.out.println("响应状态：" + statusLine.toString());
            HttpEntity httpEntity = httpResponse.getEntity();
            System.out.println("响应信息：" + httpEntity.toString());
        }


    }

    public void sendPost(String url) throws IOException {
        try(CloseableHttpClient  httpclient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("param1", "value1"));
            params.add(new BasicNameValuePair("param2", "value2"));
            params.add(new BasicNameValuePair("param3", "value3"));
            HttpEntity httpEntity = new UrlEncodedFormEntity(params);
            post.setEntity(httpEntity);
            CloseableHttpResponse  httpResponse = httpclient.execute(post);
            StatusLine statusLine = httpResponse.getStatusLine();
            System.out.println("响应状态：" + statusLine.toString());
            httpEntity = httpResponse.getEntity();
            System.out.println("响应信息：" + httpEntity.toString());
        }


    }
}
