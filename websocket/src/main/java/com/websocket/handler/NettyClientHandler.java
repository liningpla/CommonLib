package com.websocket.handler;

import android.util.Log;

import com.websocket.biz.NettyBiz;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    //通道就绪事件(就是在bootstrap启动助手配置中addlast了handler之后就会触发此事件)
    //但我觉得也可能是当有客户端连接上后才为一次通道就绪
    public void channelActive(ChannelHandlerContext ctx) {
        Log.i(NettyBiz.TAG, "Client :" + ctx);
        //向服务器端发消息
        ctx.writeAndFlush(Unpooled.copiedBuffer("你好啊！", CharsetUtil.UTF_8));
    }

    //数据读取事件
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //传来的消息包装成字节缓冲区
        ByteBuf byteBuf = (ByteBuf) msg;
        //Netty提供了字节缓冲区的toString方法，并且可以设置参数为编码格式：CharsetUtil.UTF_8
        Log.i(NettyBiz.TAG, "服务器端发来的消息：" + byteBuf.toString(CharsetUtil.UTF_8));
    }
}