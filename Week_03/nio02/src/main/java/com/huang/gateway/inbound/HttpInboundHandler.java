package com.huang.gateway.inbound;

import com.huang.gateway.filter.HttpRequestFilter;
import com.huang.gateway.filter.filterimpl.AddHeaderFilter;
import com.huang.gateway.mappool.ServicesMappingPool;
import com.huang.gateway.router.RandomHttpEndpointRouter;
import com.huang.gateway.outbound.httpclient4.MyHttpClientHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HttpInboundHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(HttpInboundHandler.class);

    /**
     * 客户端与服务端建立了通信通道并且可以传输数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        logger.info("客户端[IP:{},Port:{}]建立链接", channel.remoteAddress().getHostString(), channel.remoteAddress().getPort());
    }

    /**
     * 客户端主动断开服务端的链接
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        logger.info("客户端[IP:{},Port:{}]断开链接", channel.remoteAddress().getHostString(), channel.remoteAddress().getPort());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        SocketChannel channel = (SocketChannel) ctx.channel();
        try {
            FullHttpRequest fullRequest = (FullHttpRequest) msg;
            ServicesMappingPool pool = new ServicesMappingPool();//获取服务映射信息
            List<HttpRequestFilter> filters = pool.getFilters(fullRequest.uri());//获取过滤器集合
            for(HttpRequestFilter filter : filters){
                filter.filter(fullRequest, ctx);
            }

            /**
             * 获取服务对应的节点池，并使用随机算法转发至其中一个节点
             */
            final String backendUrl = new RandomHttpEndpointRouter().route(pool.getEndpoints(fullRequest.uri()));
            new MyHttpClientHandler().handle(backendUrl, fullRequest, ctx);

        } catch(Exception e) {
            logger.error("处理请求失败", e);
            logger.info("处理客户端[IP:{},Port:{}]请求失败", channel.remoteAddress().getHostString(), channel.remoteAddress().getPort(), e);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
    /**
     * 通信异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        logger.info("客户端[IP:{},Port:{}]通信异常", channel.remoteAddress().getHostString(), channel.remoteAddress().getPort(), cause);
        ctx.close();
    }

}
