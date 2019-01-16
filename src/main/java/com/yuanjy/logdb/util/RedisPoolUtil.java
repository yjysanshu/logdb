package com.yuanjy.logdb.util;

import io.sentry.Sentry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public final class RedisPoolUtil {

    private static Logger logger = LogManager.getLogger(RedisPoolUtil.class);

    //Redis服务器IP
    private static String ADDR = "127.0.0.1";
    //Redis的端口号
    private static Integer PORT = 6379;
    //访问密码
    private static String AUTH = "";    //Redis访问密码

    //可用连接实例的最大数目，默认为8；
    //如果赋值为-1，则表示不限制，如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)
    private static Integer MAX_TOTAL = 1024;

    //控制一个pool最多有多少个状态为idle(空闲)的jedis实例，默认值是8
    private static Integer MAX_IDLE = 200;

    //等待可用连接的最大时间，单位是毫秒，默认值为-1，表示永不超时。
    //如果超过等待时间，则直接抛出JedisConnectionException
    private static Integer MAX_WAIT_MILLIS = 10000;

    private static Integer TIMEOUT = 10000;

    //在borrow(用)一个jedis实例时，是否提前进行validate(验证)操作；
    //如果为true，则得到的jedis实例均是可用的
    private static Boolean TEST_ON_BORROW = true;

    private  static JedisPool jedisPool = null;

    /*
      静态块，初始化Redis连接池
     */
    static {
        try {
            JedisPoolConfig config = new JedisPoolConfig();

            /*
                注意：
                在高版本的jedis jar包，比如本版本2.9.0，JedisPoolConfig没有setMaxActive和setMaxWait属性了
                这是因为高版本中官方废弃了此方法，用以下两个属性替换。
                maxActive  ==>  maxTotal
                maxWait==>  maxWaitMillis
             */
            config.setMaxTotal(MAX_TOTAL);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT_MILLIS);
            config.setTestOnBorrow(TEST_ON_BORROW);
            jedisPool = new JedisPool(config,ADDR,PORT,TIMEOUT,AUTH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Jedis实例
     * @return Jedis
     */
    public synchronized static Jedis getJedis(){
        try {
            if (jedisPool != null) {
                return jedisPool.getResource();
            }
            logger.error("获取redis链接失败1");
            return null;
        } catch (Exception e) {
            logger.error("创建redis链接失败");
            logger.error(e.getMessage());
            Sentry.capture(e);
            return null;
        }
    }
}
