package jzq.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lianrongfa on 2018/5/21.
 */
public class ProtocolConfig {
    /**
     * 可接收的协议标识集合
     */
    private final List<Integer> receiveContainer=new ArrayList<Integer>();

    /**
     * 各个协议标识对应的数据长度
     */
    private final Map<Integer,Integer> receiveMap=new HashMap<Integer, Integer>();

    public List<Integer> getReceiveContainer() {
        return receiveContainer;
    }

    public Map<Integer, Integer> getReceiveMap() {
        return receiveMap;
    }

    public Integer getLength(Integer type){
        if(type!=null) {
            return receiveMap.get(type);
        }
        return null;
    }

    public boolean addReceiveType(Integer item){
        if(item!=null){
            return receiveContainer.add(item);
        }
        return false;
    }

    public boolean removeReceiveType(Byte item){
        if(item!=null){
           return receiveContainer.remove(item);
        }
        return false;
    }

    public Integer addReceiveLength(Integer type, Integer length){
        if(type!=null&&length!=null){
            return receiveMap.put(type,length);
        }
        return null;
    }

    public Integer removeReceiveLength(Byte type){
        if(type!=null){
            return receiveMap.remove(type);
        }
        return null;
    }
}
