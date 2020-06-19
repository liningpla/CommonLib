package com.websocket;


import android.util.Log;

import com.websocket.biz.NettyBiz;
import com.websocket.handler.NettyServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**服务端操作类*/
public class NettyServer {

    public static void init(int port){

        try {
            //创建一个线程组，接收客户端的连接
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            //创建一个线程组，用于处理网络操作
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            //创建服务器端启动助手（用于配置参数）
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)//设置两个线程组
                    .channel(NioServerSocketChannel.class)//精华部分，设置通道的底层实现，
                    //通过NioServerSocketChannel
                    //这也是Netty的与NIO搭配的地方(此处作为服务器端通道的实现)
                    .option(ChannelOption.SO_BACKLOG, 12)//设置线程队列中等待连接的个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //是否启用心跳保活机制。在双方TCP套接字建立连接后（即都进入ESTABLISHED状态）并
                    //且在两个小时左右
                    //上层没有任何数据传输的情况下，这套机制才会被激活。
                    .childHandler(new ChannelInitializer<SocketChannel>() {//(用内部类的方法)
                        //创建一个通道初始化对象
                        public void initChannel(SocketChannel sc) {
                            sc.pipeline().addLast(new NettyServerHandler());//往pipeline链中添加
                            //自定义的handler类
                        }
                    });
            Log.i(NettyBiz.TAG, "...Server is Ready...");
            //ChannelFuture接口，用于在之后的某个时间点确定结果
            ChannelFuture sf = serverBootstrap.bind(port).sync();//绑定端口 非阻塞 异步
            Log.i(NettyBiz.TAG, "....Server is Start....");
            //关闭通道，关闭线程组
            sf.channel().closeFuture().sync();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
