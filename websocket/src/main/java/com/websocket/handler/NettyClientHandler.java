package com.websocket.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            //强制类型转换
            ByteBuf readBuffer = (ByteBuf) msg;
            // 创建一个字节数组，用于保存缓存中的数据。readableBytes 在原生的ByteBuffer也有同样的功能的方法
            byte[] tempDatas = new byte[readBuffer.readableBytes()];
            //读取到对应的数据 可以直接读取，这个不需要考虑复位的问题，
            readBuffer.readBytes(tempDatas);
            System.out.println("from server " + new String(tempDatas, "UTF-8"));
        }finally {
            //用于避免内存溢出
            ReferenceCountUtil.release(msg);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("client exceptionCaught method run …… ");
        ctx.close();
    }

}