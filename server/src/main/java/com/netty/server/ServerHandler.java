package com.netty.server;

import com.netty.client.Request;
import com.netty.client.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Request request = (Request)msg;
        System.out.println("Server:" + request.getId() + "-" + request.getName() + "-" + request.getMsg());
        Response resp = new Response();
        resp.setId(request.getId() + 1);
        resp.setName("resp:"+request.getName());
        resp.setMsg("resp:" + request.getMsg());
        ctx.writeAndFlush(resp);
        /*String data = (String) msg;
        System.out.println("Server : " + new String(data));
        ctx.writeAndFlush(Unpooled.copiedBuffer("服务器响应$_".getBytes())).addListener(ChannelFutureListener.CLOSE);*/
        /*ByteBuf buf = (ByteBuf)msg;
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        String request = new String(data, "utf-8");
        System.out.println("Server: " + request);
        //写给客户端
        String response = "我是反馈信息";
        ctx.writeAndFlush(Unpooled.copiedBuffer("8888".getBytes()));*/
    }
}






















