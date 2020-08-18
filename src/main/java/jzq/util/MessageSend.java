package jzq.util;

//import corss.server.netty.NettyContainer;

import io.netty.channel.Channel;
import jzq.server.netty.NettyContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author leon
 */
public class MessageSend implements Runnable {
    private final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
    private static final Logger logger = LoggerFactory.getLogger(MessageSend.class);

    @Override
    public void run() {
        //取设备id
        Set<Channel> keySet = NettyContainer.messageStack.keySet();
        for (Channel ch : keySet) {
            List<byte[]> list = NettyContainer.messageStack.get(ch);
            if (!list.isEmpty()) {
                String id = "";
                id = NettyContainer.SOURCE_IDS.get(ch);
                System.out.println("channel id {%d}"+id);
                byte[] bs = list.get(list.size() - 1);
                //依次输出消息
                if (ch.isActive()) {
                    String test ="test";
                    ch.writeAndFlush(bs);
                    System.out.println("输出数据:" + Arrays.toString(bs));
                    list.remove(list.size() - 1);
                } else {
                    //去除通道
                    System.out.println("输出通道已关闭");
                    NettyContainer.GROUP.remove(ch);
                    NettyContainer.SOURCE_CHANNELS.remove(ch);
                    if (id != null || !"".equals(id)) {
                        NettyContainer.SOURCE_IDS.remove(id);
                    }

                    sendFile(id.getBytes(), bs);
                    list.remove(list.size() - 1);
                }
            }
        }
    }

    public void executor() {
        scheduledThreadPool.scheduleWithFixedDelay(this, 1000, 3000, TimeUnit.MILLISECONDS);//首次延迟1分钟 每隔30秒执行一次
    }

    @SuppressWarnings("unused")
    private void sendFile(byte[] ids, byte[] data) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String path = System.getProperty("user.dir") + "/Data";
        String name = "/" + df.format(new Date()) + ".txt";
        File iofile = new File(path + name);
        File filemkdirs = new File(path);
        FileOutputStream fw = null;
        try {
            if (!filemkdirs.exists()) {
                filemkdirs.mkdirs();
                File file = new File(path + name);
                if (file.exists()) {
                    file.createNewFile();
                }
            }
            byte[] idData = new byte[ids.length + 1];
            for (int i = 0; i < ids.length; i++) {
                idData[i] = ids[i];
            }
            idData[ids.length] = 0x09;
            fw = new FileOutputStream(iofile, true);

            fw.write(idData);
            fw.write(data);

        } catch (Exception e) {
            logger.error("生成数据备份失败" + e.getMessage());
        } finally {
            try {
                fw.flush();
                fw.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

    }
}
