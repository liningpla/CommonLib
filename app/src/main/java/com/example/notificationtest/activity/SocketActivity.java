package com.example.notificationtest.activity;

import android.os.Bundle;
import android.view.View;


import com.common.BaseAcivity;
import com.example.notificationtest.R;
import com.websocket.biz.NettyBiz;

import org.jetbrains.annotations.Nullable;

public class SocketActivity extends BaseAcivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);
        findViewById(R.id.btn_startServer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startServer();
            }
        });

        findViewById(R.id.btn_connectServer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectServer();
            }
        });

        findViewById(R.id.btn_clientSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientSend();
            }
        });

        findViewById(R.id.btn_serverSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverSend();
            }
        });
    }

    /**开启服务端连接通道*/
    public void startServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyBiz.INIT.startServerConnect(SocketActivity.this);
            }
        }).start();
    }

    /**客户端连接服务端*/
    public void connectServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyBiz.INIT.clientConnect(SocketActivity.this);
            }
        }).start();
    }

    /**客户端发送服务端*/
    public void clientSend(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyBiz.INIT.clientSendMsg("这是客户端单独发送的");
            }
        }).start();
    }

    /**服务端发送客户端*/
    public void serverSend(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyBiz.INIT.severSendMsg("这是服务端单独发送的");
            }
        }).start();
    }

}
