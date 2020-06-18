package com.websocket.biz;

import android.content.Context;
import android.util.Log;

import com.websocket.NettyClient;
import com.websocket.NettyServer;
import com.websocket.handler.NettyClientHandler;
import com.websocket.handler.NettyServerHandler;
import com.websocket.utils.DeviceUtils;

import java.net.URI;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public enum  NettyBiz {
    INIT;

    public static final String TAG = "NettyBiz";


    /**客户端链接服务端*/
    public void clientConnect(Context context){
        String ipHost = DeviceUtils.getIPAddress(context);
        Log.i(TAG, "-----------ipHost = "+ipHost);
        NettyClient client = null;
        ChannelFuture future = null;
        try{
            client = new NettyClient();
            future = client.doRequest(ipHost,8081,new NettyClientHandler());
            Scanner scanner = null;
            while (true){
                scanner = new Scanner(System.in);
                Log.i(TAG, "enter message send to server (enter exit close client)");
                String line = scanner.nextLine();
                if ("exit".equals(line)){
                    future.channel().writeAndFlush(Unpooled.copiedBuffer(line.getBytes("UTF-8"))).addListener(ChannelFutureListener.CLOSE);
                    break;
                }
                future.channel().writeAndFlush(Unpooled.copiedBuffer(line.getBytes("UTF-8")));
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "clientConnect error = "+e.getLocalizedMessage());
        }finally {
            if (null!=client){
                client.release();
            }
            if (null!=future){
                try {
                    future.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**启动创建服务端的连接通道*/
    public void startServerConnect(Context context){
        ChannelFuture future = null;
        NettyServer server = null;
        try{
            server = new NettyServer();
            //建立连接
            future = server.doAccept(8081, new NettyServerHandler());
            Log.i(TAG, "server started.");
            //关闭连接，回收资源
            future.channel().closeFuture();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i(TAG, "server error."+e.getLocalizedMessage());
        }finally {
            if (null != future){
                try {
                    future.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (null!=server){
                server.release();
            }
        }
    }
}
