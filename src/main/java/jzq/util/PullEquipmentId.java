package jzq.util;

import io.netty.channel.Channel;
import jzq.server.netty.NettyContainer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by lianrongfa on 2018/6/13.
 */
public class PullEquipmentId implements Runnable {

    private final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);

    private final byte [] uart=new byte[]{100,0x31,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x0D,0x0A};
    @Override
    public void run() {
        //取设备id
        for (Channel channel : NettyContainer.GROUP) {
        	String id = NettyContainer.SOURCE_IDS.get(channel);
        	if(id==null||"".equals(id)){//针对未注册的通道进行心跳监测
        		if (channel.isActive()) {
//                  channel.writeAndFlush(uart);
                  NettyContainer.addMessage(channel,uart);
              }
        	}
        }
    }

    public void executor() {
        scheduledThreadPool.scheduleWithFixedDelay(this, 60000, 30000, TimeUnit.MILLISECONDS);//首次延迟1分钟 每隔30秒执行一次
    }
}
