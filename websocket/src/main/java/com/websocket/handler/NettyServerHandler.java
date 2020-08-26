package com.websocket.handler;

import android.os.Environment;
import android.util.Log;

import com.websocket.biz.NettyBiz;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.util.CharsetUtil;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    //数据读取事件
    public void channelRead(ChannelHandlerContext ctx,Object msg){
//        readMsag(msg);
        readFile(ctx, msg);
    }

    private void readFile(ChannelHandlerContext ctx, Object msg){
        try{
            if (msg instanceof FileRegion) {
                String filepath = Environment.getExternalStorageDirectory() + File.separator + "Download/greentea_server.apk";
                RandomAccessFile randomAccessFile=new RandomAccessFile(filepath, "rw");
                FileRegion region = (FileRegion) msg;
                ctx.write(new DefaultFileRegion(randomAccessFile.getChannel(), 0, region.count()));
                Log.i(NettyBiz.TAG, "客户端发来的文件接收");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void readMsag(Object msg){
        //传来的消息包装成字节缓冲区
        ByteBuf byteBuf = (ByteBuf) msg;
        //Netty提供了字节缓冲区的toString方法，并且可以设置参数为编码格式：CharsetUtil.UTF_8
        Log.i(NettyBiz.TAG, "客户端发来的消息：" + byteBuf.toString(CharsetUtil.UTF_8));
    }

    //数据读取完毕事件
    public void channelReadComplete(ChannelHandlerContext ctx){
        //数据读取完毕，将信息包装成一个Buffer传递给下一个Handler，Unpooled.copiedBuffer会返回一个Buffer
        //调用的是事件处理器的上下文对象的writeAndFlush方法
        //意思就是说将  你好  传递给了下一个handler
        Log.i(NettyBiz.TAG, "客户端发来的数据读完完成");
        ctx.writeAndFlush(Unpooled.copiedBuffer("你好!", CharsetUtil.UTF_8));

    }

    //异常发生的事件
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
        //异常发生时关闭上下文对象
        ctx.close();
    }
}
