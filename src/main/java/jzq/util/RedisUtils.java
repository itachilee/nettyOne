package jzq.util;

import jzq.configuration.ConfigContext;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author lianrongfa
 * @date 2018/8/24
 */
public abstract class RedisUtils {

    private static JedisPool pool;

    static{
        buildJedisPool();
    }

    private static void buildJedisPool(){
        ConfigContext instace = ConfigContext.getInstance();
        String server=instace.getRedisServer();
        int port=instace.getRedisPort();
        pool=new JedisPool(server,port);
    }

    public static synchronized Jedis getJedis(){

        if(pool==null){
            buildJedisPool();
        }
        Jedis jedis = pool.getResource();

        return jedis;
    }
 }
