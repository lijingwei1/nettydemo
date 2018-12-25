package com.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class Server {
    private int port;
    public Server(int port){
        this.port = port;
    }
    public void run(){
        EventLoopGroup bossGroup = new NioEventLoopGroup();//用于处理服务器端接收客户端连接
        EventLoopGroup workerGroup = new NioEventLoopGroup();//进行网络通信（读写）
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();//辅助工具类，用于服务器通道的一系列配置
            bootstrap.group(bossGroup, workerGroup)//绑定两个线程组
                .channel(NioServerSocketChannel.class)//指定NIO的模式
                .childHandler(new ChannelInitializer<SocketChannel>(){//配置具体的数据处理方式
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        /*//设置特殊分隔符
                        ByteBuf buf = Unpooled.copiedBuffer("$_".getBytes());
                        socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(128, buf));
                        //设置字符串形式的解码
                        socketChannel.pipeline().addLast(new StringDecoder());
                        socketChannel.pipeline().addLast(new ServerHandler());*/
                        //设置5秒没有读取数据就断开连接，减少服务端资源占用
                        //socketChannel.pipeline().addLast(new ReadTimeoutHandler(5));
                        socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                        socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                        socketChannel.pipeline().addLast(new ServerHandler());
                    }
                })
                /*
                对于ChannelOption.SO_BACKLOG的解释：
                服务器端TCP内核维护两个队列，我们称之为A、B队列。客户端向服务器端connoct时，会发送带有SYN标志的包（第一次握手），服务器端
                接收到客户端发送的SYN时，向客户端发送SYN ACK确认（第二次握手），此时TCP内核模块把客户端连接加入到A队列中，然后服务器接收到
                客户端发送的ACK时（第三次握手），TCP内核模块把客户端连接从A队列移到B队列，连接完成，应用程序的accept会返回，也就是说accept
                从B队列中取出完成了三次握手的连接。
                A队列和B队列的长度之和就是backlog。当A、B队列的长度之和大于ChannelOption.SO_BACKLOG时，新的连接将会被TCP内核拒绝
                所以，如果backlog过小，可能会出现accept速度跟不上，A、B队列满了，导致新的客户端无法连接。要注意的是，backlog对程序
                支持的连接数并无影响，backlog影响的只是还没被accept取出的连接
                 */
                .option(ChannelOption.SO_BACKLOG, 128)//设置TCP缓冲区
                .option(ChannelOption.SO_SNDBUF, 32*1024)//设置发送数据缓冲大小
                .option(ChannelOption.SO_RCVBUF, 32*1024)//设置接受数据缓冲大小
                .childOption(ChannelOption.SO_KEEPALIVE, true);//保持连接
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new Server(8379).run();
    }
}





































































