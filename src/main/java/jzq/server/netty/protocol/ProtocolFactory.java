package jzq.server.netty.protocol;


import io.netty.buffer.ByteBuf;
import jzq.server.netty.protocol.receive.*;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by lianrongfa on 2018/5/22.
 */
public class ProtocolFactory {

    private static Map<Integer, Class> map = new HashMap<Integer, Class>();

    static {
        //可用配置文件方式

        map.put(160, FaultUART.class);


    }


    public static UART createUART(int mark, ByteBuf data) {
        Class clazz = map.get(mark);
        if (clazz != null) {
            UART uart;
            try {
                uart = (UART) clazz.newInstance();
                uart.setData(data);
                uart.parse();
                return uart;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

//    public static UART createUART(int mark, byte[] data) {
//        Class clazz = map.get(mark);
//        if (clazz != null) {
//            UART uart;
//            try {
//                uart = (UART) clazz.newInstance();
//                uart.setData(data);
//                uart.parse();
//                return uart;
//            } catch (InstantiationException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }
}
