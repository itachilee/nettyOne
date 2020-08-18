package jzq.proxy;


import io.netty.channel.ChannelHandlerContext;
import jzq.controller.ChannelController;
import jzq.controller.Controller;
import jzq.controller.FaultController;
import jzq.server.netty.protocol.UART;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lianrongfa on 2018/5/31.
 */
public class SimpleFactory {

    private static Map<Byte,Class> controllerMap =new HashMap<Byte,Class>();

    static {


        controllerMap.put((byte) 160,FaultController.class);
        controllerMap.put((byte) 70, ChannelController.class);

    }

    public static Controller createController(ChannelHandlerContext ctx, UART uart){

        byte mark = (byte)160;
                //uart.getMark();
        Class clazz = controllerMap.get(mark);
        Controller controller = null;
        if (clazz!=null){
            try {
                Constructor constructor = clazz.getConstructor(ChannelHandlerContext.class, UART.class);

                controller = (Controller) constructor.newInstance(ctx, uart);

                ProxyController proxy = new ProxyController(controller);

                Controller controllerProxy = proxy.getInstace();
                return controllerProxy;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
