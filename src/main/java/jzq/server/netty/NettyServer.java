package jzq.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import jzq.configuration.ConfigContext;
import jzq.server.netty.codec.UARTDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @date 2018/5/17
 */
public class NettyServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private Integer port;
    private SocketChannel socketChannel;

    public NettyServer(Integer port) throws Exception {
        this.port = port;
        bind(port);
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    private void bind(int serverPort) throws Exception {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker);
        bootstrap.channel(NioServerSocketChannel.class);
        // 保持连接数
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024 * 1024);
        // 有数据立即发送
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        // 保持连接
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        // 处理新连接
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel sc) throws Exception {
                // 增加任务处理
                ChannelPipeline p = sc.pipeline();
                p.addLast(
                        new UARTDecoder(),
                        new ByteArrayEncoder(),
                        new ServerHandler());
            }}
        );

        ChannelFuture f = bootstrap.bind(serverPort).sync();
        if (f.isSuccess()) {
            logger.info("long connection started success port:"+ ConfigContext.getInstance().getServerPort());
        } else {
            logger.error("long connection started fail");
        }
    }
}
