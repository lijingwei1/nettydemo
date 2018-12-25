package com.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.ArrayList;
import java.util.List;

public class Client {
    private static class SingletonHolder{
        static final Client instance = new Client();
    }
    private static Client getInstance(){
        return SingletonHolder.instance;
    }
    private EventLoopGroup workGroup;
    private Bootstrap bootstrap;
    private static ChannelFuture cf;

    private Client(){
        workGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(workGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //设置5秒没有读取数据就断开连接，减少服务端资源占用
                        socketChannel.pipeline().addLast(new ReadTimeoutHandler(2));
                        socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                        socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                        socketChannel.pipeline().addLast(new ClientHandler());
                    }
                });
    }
    public void connect(){
        try{
            this.cf = bootstrap.connect("127.0.0.1", 8379).sync();
            System.out.println("客户端已远程连接到服务端，可以进行数据的交换。。。");
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
    public ChannelFuture getChannelFuture(){
        if(null == this.cf){
            this.connect();
        }
        if(!this.cf.channel().isActive()){
            this.connect();
        }
        return this.cf;
    }
    private void shutdown(){
        workGroup.shutdownGracefully();
    }
    private static List<Integer> times = new ArrayList<>();
    public static void main(String[] args) throws Exception{
        for (int i=0; i<11; i++){
            times.add(10-i);
        }
        final Client c = Client.getInstance();
        c.getChannelFuture();
        /*future.channel().writeAndFlush(Unpooled.copiedBuffer("select * from acct_item where acct_item_id=1000$_".getBytes()));*/
        /*while (times.size() > 0){
            future.channel().writeAndFlush(Unpooled.copiedBuffer("select * from acct_item where acct_item_id=1000".getBytes()));
            times.remove(0);
            Thread.sleep(1000);
        }*/
        for(int i=1; i <= 5; i++){
            Request req = new Request();
            req.setId(i);
            req.setName("student" + i);
            req.setMsg("数据信息"+i);
            cf.channel().writeAndFlush(req);
        }
        //异步等待关闭通道
        cf.channel().closeFuture().sync();
        System.out.println("------------------------"+cf.channel().isActive());
        cf = c.getChannelFuture();
        Request r = new Request();
        r.setId(100);
        r.setName("ljw");
        r.setMsg("复活了！！！！");
        cf.channel().writeAndFlush(r);
        cf.channel().closeFuture().sync();
        c.shutdown();
    }
}
