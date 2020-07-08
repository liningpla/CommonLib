package com.websocket.biz;

import android.content.Context;
import android.util.Log;

import com.websocket.NettyClient;
import com.websocket.NettyServer;
import com.websocket.utils.DeviceUtils;

public enum  NettyBiz {
    INIT;

    public static final String TAG = "NettyBiz";

    /**客户端链接服务端*/
    public void clientConnect(Context context){
        String ipHost = DeviceUtils.getIPAddress(context);
        Log.i(TAG, "------clientConnect-----ipHost = "+ipHost);
        NettyClient.INIT.init(ipHost, 9999);

    }

    /**启动创建服务端的连接通道*/
    public void startServerConnect(Context context){
        String ipHost = DeviceUtils.getIPAddress(context);
        Log.i(TAG, "------startServerConnect-----ipHost = "+ipHost);
        NettyServer.INIT.init(9999);
    }

    /**客户端发送消息*/
    public void clientSendMsg(String msg){
        NettyClient.INIT.sendMessage(msg);
    }

    /**服务端发送消息*/
    public void severSendMsg(String msg){
        NettyServer.INIT.severSendMsg(msg);
    }
}
