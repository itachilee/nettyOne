package jzq.server.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jzq.controller.Controller;
import jzq.proxy.SimpleFactory;
import jzq.server.netty.protocol.UART;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lianrongfa on 2018/5/17.
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    private static final ExecutorService webExecutors = Executors.newCachedThreadPool();

    /**
     * 客户端与服务端创建连接的时候调用
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        SocketAddress address = channel.remoteAddress();
        NettyContainer.GROUP.add(channel);
        logger.info(address + ":客户端与服务端连接开始...");

        //发设备ID请求
        byte[] uart = new byte[]{100, 0x31, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x0D, 0x0A};

        if (channel.isActive()) {
            NettyContainer.addMessage(channel, uart);
        }

        //接通后取设备id  ps:经沟通,改为设备通电一分钟后自动上传设备id
    }

    /**
     * 客户端与服务端断开连接时调用
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //SocketAddress address = channel.remoteAddress();
        NettyContainer.GROUP.remove(channel);

        String id = NettyContainer.SOURCE_IDS.get(channel);
        NettyContainer.SOURCE_IDS.remove(channel);
        NettyContainer.SOURCE_CHANNELS.remove(id);

        NettyContainer.WARN_SET.add(id);
        logger.warn("设备：" + id + " 与服务端连接关闭...");
    }

    /**
     * 服务端接收客户端发送过来的数据结束之后调用
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
        System.out.println("信息接收完毕...");
//        String id = NettyContainer.sourceIds.get(ctx.channel());
//        if("".equals(id) || null==id ){
//        	System.out.println("信息接收完毕...");
//        	return;
//        }
//        Channel channel = NettyContainer.sourceChannels.get(id);
//        System.out.println("得到"+id+"==="+channel);
//        System.out.println("信息接收完毕...");
//        if("".equals(id) || null==id || null==channel || !channel.isActive()){
//        	NettyContainer.sourceChannels.remove(id);
//        	NettyContainer.sourceIds.remove(ctx.channel());
//        	NettyContainer.group.add(ctx.channel());
//        }
    }

    /**
     * 工程出现异常的时候调用
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //ctx.close();
    }

    /**
     * 服务端(中间件)处理客户端(硬件)请求的核心方法，这里接收了客户端(硬件)发来的信息
     */
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object info) throws Exception {

        UART uart;
        try {
            uart = (UART) info;
        } catch (Exception e) {
            logger.error("协议错误");
            return;
        }
        String id = NettyContainer.SOURCE_IDS.get(channelHandlerContext.channel());
        logger.info("服务端接收到设备：" + id + " 的数据：" + uart.toString());
        //回复设备、回复信息为前两位
//        if(uart.getMark()!=104){
        byte[] bytes = buildData(uart);
        String reply = "";
        for (int i = 0; i < bytes.length; i++) {
            reply += bytes[i] + " ";
        }
        logger.info("服务端回复设备：" + id + " 数据：" + reply);
//            channelHandlerContext.writeAndFlush(bytes);
        //更改为消息队列
        NettyContainer.addMessage(channelHandlerContext.channel(), bytes);
//        }

        Controller controller = SimpleFactory.createController(channelHandlerContext, uart);
        if (controller != null) {
            controller.executor();
        } else {
            logger.warn("未找到对应的控制器!");
        }
    }

    private byte[] buildData(UART uart) {
        byte[] bytes = new byte[16];
        for (int i = 0; i < 16; i++) {
            bytes[i] = 0x20;
        }
        bytes[0] = 104;
//        bytes[1]=uart.getMark();
//        bytes[2]=uart.getMark();
        bytes[14] = 0x0D;
        bytes[15] = 0x0A;
        return bytes;
    }
}
