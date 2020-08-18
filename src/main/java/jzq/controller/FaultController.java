package jzq.controller;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import jzq.server.netty.NettyContainer;
import jzq.server.netty.protocol.UART;
import jzq.server.netty.protocol.receive.FaultUART;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FaultController extends AbstractController {

    //    private final static String method = "/dkProblem.do?method=saveProblem";
    private static final Logger logger = LoggerFactory.getLogger(ChannelController.class);

    public FaultController(ChannelHandlerContext ctx, UART info) {
        super(ctx, info);
    }

    @Override
    public String executor() {
        if (info instanceof FaultUART) {
            FaultUART faultUART = (FaultUART) this.info;

            JSONObject jsonObject = new JSONObject();

            String msg = jsonObject.toJSONString();
            msg = "faultJson=" + msg;

//            String result = HttpUtil.httpRequest(getUrl() + method, "POST", msg);
            String result = "this is no network";
            System.out.println("FaultController");
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
                byte[] testData = new byte[]{54, 0x33, 0x21, 0x21, 0x21, 0x21, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x0D, 0x0A};
                message.put(c, new ArrayList<byte[]>());
                NettyContainer.addMessage(c, testData);

                //ui data
//                buildUiData(idString);
//                NettyContainer.addMessage(channel, uart);
                NettyContainer.WARN_SET.remove(idString);

                logger.info("设备：" + idString + " 与通道关联成功.");
            }
            return result;
        }

        return null;
    }
}
