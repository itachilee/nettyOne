package jzq.server.netty;

//import corss.ui.data.ChannelState;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lianrongfa on 2018/5/17.
 */
public class NettyContainer {

    /**
     * 存储每一个客户端接入进来时的channel对象
     */
    public final static ChannelGroup GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 通道对应设备id
     */
    public final static Map<Channel,String> SOURCE_IDS =new ConcurrentHashMap<Channel,String>();

    /**
     * 通道对应设备id
     */
    public final static Map<String,Channel> SOURCE_CHANNELS =new ConcurrentHashMap<String,Channel>();


    /**
     * 通道断开设备id集合
     */
    public final static Set<String> WARN_SET = Collections.synchronizedSet(new HashSet<String>());

    /**
     * ui展示数据
     */
//    public final static Map<String,ChannelState> uiData=new ConcurrentHashMap<String,ChannelState>();
    
    /**
     * 消息控制
     */
    public static  Map<Channel,List<byte[]>> messageStack=new ConcurrentHashMap<Channel, List<byte[]>>();
    
    /**
     * 写入数据
     * @param id
     * @param data
     */
    public static void addMessage(Channel channel,byte[] Msgdata){
    	if(!NettyContainer.messageStack.containsKey(channel)){
    		messageStack.put(channel, new ArrayList<byte[]>());
    	}
    	List<byte[]> list = NettyContainer.messageStack.get(channel);
        list.add(Msgdata);
    }
    
    /**
     * 获得剩余全部的消息数量
     * @return
     */
    public static int getMesCount(){
    	int count=0;
    	Collection<List<byte[]>> values = NettyContainer.messageStack.values();
    	for(List<byte[]> list:values){
    		if(list==null) {
                continue;
            }
    		count+=list.size();
    	}
    	return count;
    }
    
}