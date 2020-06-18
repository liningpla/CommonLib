package com.websocket;

import com.websocket.handler.NettyClientHandler;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
    private static final String TAG = "NettyClient";
    //处理请求线程组
    private EventLoopGroup group = null;
    //服务启动相关配置信息
    private Bootstrap bootstrap = null;
    public NettyClient(){
        init();
    }
    private void init(){
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        //定义线程组
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
    }
    public ChannelFuture doRequest(String host, int port, final ChannelHandler... handlers) throws InterruptedException {
        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            /**
             * 客户端的Handler没有childHandler方法，只有Handler方法
             * 这个方法与服务器的方法是类似的。
             * 客户端必须绑定处理器，也就说必须调用Handler方法
             * @param ch
             * @throws Exception
             */
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(handlers);
            }
        });
        //建立连接
        ChannelFuture future = this.bootstrap.connect(host,port).sync();
        return future;
    }
    public void release(){
        this.group.shutdownGracefully();
    }

}