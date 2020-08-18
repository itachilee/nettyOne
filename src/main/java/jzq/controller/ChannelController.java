package jzq.controller;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import jzq.server.netty.NettyContainer;
import jzq.server.netty.protocol.UART;

import jzq.server.netty.protocol.receive.FaultUART;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by lianrongfa on 2018/6/5.
 * 设备id与通道关系控制器
 */
public class ChannelController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(ChannelController.class);

    public ChannelController(ChannelHandlerContext ctx, UART info) {
        super(ctx, info);
    }

    @Override
    public String executor() {

        if (this.info instanceof FaultUART) {
            String idString = "12";//this.info.getIdString();
            Channel c = this.getChannel();
            Map<String, Channel> sourceChannels = NettyContainer.SOURCE_CHANNELS;
            Map<Channel, String> sourceIds = NettyContainer.SOURCE_IDS;
            Map<Channel, List<byte[]>> message = NettyContainer.messageStack;
            if (idString != null && c != null) {
                Channel channel = sourceChannels.get(idString);
                if (channel != null && !channel.equals(c)) {
                    logger.warn("设备：" + idString + " 已与通道关联，已将此前设备通道关闭，请检查设备id是否重复！");
                    channel.close();
                }
                NettyContainer.SOURCE_CHANNELS.put(idString, c);
                NettyContainer.SOURCE_IDS.put(c, idString);
                //新增消息队列
//                message.put(c, new ArrayList<byte[]>());

                //ui data
//                buildUiData(idString);

                NettyContainer.WARN_SET.remove(idString);

                logger.info("设备：" + idString + " 与通道关联成功.");
            }
        }
        return null;
    }


}
