package com.websocket;

import android.os.Environment;
import android.util.Log;

import com.websocket.biz.NettyBiz;
import com.websocket.handler.NettyClientHandler;

import java.io.File;
import java.io.RandomAccessFile;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FileRegion;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

/**客户端操作类*/
public enum  NettyClient {
    INIT;
    private Channel channel;
    private EventLoopGroup group;

    public void init(String ipHost, int port){
        try{
            //创建一个线程组(不像服务端需要有连接等待的线程池)
            group = new NioEventLoopGroup();
            //创建客户端的服务启动助手完成相应配置
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {//创建一个通道初始化对象
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new NettyClientHandler());//往pipeline中添加自定义的handler
                        }
                    });
            Log.i(NettyBiz.TAG, "...Client is Ready...");
            //启动客户端去连接服务器端(通过启动助手)
            ChannelFuture cf = b.connect(ipHost, port).sync();
            channel = cf.channel();
            //关闭连接(异步非阻塞)
        }catch (Exception e){
            e.printStackTrace();
            releaseChient();
        }
    }

    /**发送消息*/
    public void sendMessage(String msg){
        if(channel.isActive()){
            channel.writeAndFlush(Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
        }
        sendFile();
    }

    /**发送消息*/
    public void sendFile(){
        try{
            if(channel.isActive()){
                //向服务器端发消息
                String filepath = Environment.getExternalStorageDirectory() + File.separator + "Download/greentea.apk";
                Log.i(NettyBiz.TAG, filepath);
                RandomAccessFile randomAccessFile=new RandomAccessFile(filepath, "r");
                FileRegion region=new DefaultFileRegion(
                        randomAccessFile.getChannel(), 0, randomAccessFile.length());
                channel.write(region);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**关闭通道*/
    public void closeChannel(){
        if(channel != null){
            try {
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**关闭客户端*/
    public void releaseChient(){
        if(group != null){
            group.shutdownGracefully();
        }
    }
}