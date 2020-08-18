package jzq.proxy;


import jzq.controller.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by lianrongfa on 2018/5/23.
 */
public class ProxyController implements InvocationHandler{

    private static final Logger logger=LoggerFactory.getLogger(Controller.class);

    private Controller target;

    public ProxyController(Controller target) {
        this.target = target;
    }

    public Controller getInstace() {
        Class<? extends Controller> clazz = target.getClass();

        Class<?>[] interfaces = clazz.getSuperclass().getInterfaces();

        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, this);
        return (Controller)o;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //预留逻辑

        Object o=null;
        try {
            o = method.invoke(target, args);
        } catch (Exception e) {
            logger.error("控制器调用出错!",e);
            throw new RuntimeException(e);
        }

        //预留逻辑

        return o;
    }

}
