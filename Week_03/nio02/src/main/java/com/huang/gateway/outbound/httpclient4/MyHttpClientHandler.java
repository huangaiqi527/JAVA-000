package com.huang.gateway.outbound.httpclient4;


import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.HttpVersion;
import org.apache.http.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class MyHttpClientHandler {
    private static Logger logger = LoggerFactory.getLogger(MyHttpClientHandler.class);

    public void handle(final String backendUrl, final FullHttpRequest fullRequest, final ChannelHandlerContext ctx) {
        final String url = backendUrl + fullRequest.uri();
        sendGet(fullRequest, ctx, url);
    }

    public void sendGet(final FullHttpRequest request, final ChannelHandlerContext ctx, final String url) {
        try(CloseableHttpClient  httpclient = HttpClients.createDefault()) {
            logger.info("开始转发请求至后台服务，url:{}", url);

            HttpGet get = new HttpGet(url);
            Iterator<Map.Entry<String, String>> headers = request.headers().iteratorAsString();
            Map.Entry<String, String> entry = null;
            while (headers.hasNext()) {
                entry = headers.next();
                get.setHeader(entry.getKey(), entry.getValue());
            }
            CloseableHttpResponse  httpResponse = httpclient.execute(get);
            logger.info("成功转发请求至后台服务，url:{},response:{}", url, httpResponse.toString());
            handleResponse(request, ctx, httpResponse);
        } catch (IOException e) {
            logger.info("开始转发请求至后台服务，url:{}失败", url, e);
            handleExceptionCaught(ctx, e);
        }


    }

    public void sendPost(final FullHttpRequest request, final ChannelHandlerContext ctx, final String url){
        try(CloseableHttpClient  httpclient = HttpClients.createDefault()) {
            logger.info("开始转发请求至后台服务，url:{}", url);
            HttpPost post = new HttpPost(url);
            Iterator<Map.Entry<String, String>> headers = request.headers().iteratorAsString();
            Map.Entry<String, String> entry = null;
            while (headers.hasNext()) {
                entry = headers.next();
                post.setHeader(entry.getKey(), entry.getValue());
            }
            CloseableHttpResponse  httpResponse = httpclient.execute(post);
            logger.info("成功转发请求至后台服务，url:{},response:{}", url, httpResponse.toString());
            handleResponse(request, ctx, httpResponse);
        } catch (IOException e) {
            logger.info("开始转发请求至后台服务，url:{}失败", url, e);
            handleExceptionCaught(ctx, e);
        }


    }

    private void handleResponse(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, final HttpResponse endpointResponse)  {
        FullHttpResponse response = null;
        try {


            byte[] body = EntityUtils.toByteArray(endpointResponse.getEntity());

            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(body));
            response.headers().set("Content-Type", "application/json");
            response.headers().setInt("Content-Length", Integer.parseInt(endpointResponse.getFirstHeader("Content-Length").getValue()));

            for (Header e : endpointResponse.getAllHeaders()) {
                response.headers().set(e.getName(),e.getValue());
            }

        } catch (Exception e) {
            response = exceptionCaught(ctx, e);
        } finally {
            if (fullRequest != null) {
                if (!HttpUtil.isKeepAlive(fullRequest)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    ctx.write(response);
                }
            }
            ctx.flush();
        }

    }

    public FullHttpResponse exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        FullHttpResponse response = null;
        response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        response.headers().set("Content-Type", "application/json");
        response.headers().setInt("Content-Length", response.content().readableBytes());
        return response;
    }

    public void handleExceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.write(exceptionCaught(ctx, cause));
    }
}
