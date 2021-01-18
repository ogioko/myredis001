package com.yalong.jedis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Tuple;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author wwwya
 * @auther levithanlee
 * @ccreate--
 */
public class RedisUtil {
    public static void main(String[] args) {


        //Jedis jedis = new Jedis("hadoop102", 6379); 普通方式
        Jedis jedis = RedisUtil.getJedis();  //连接池的方式


        //set
        String set1 = jedis.set("k11", "v22");
        System.out.println(set1);

        //get
        String get1 = jedis.get("k11");
        System.out.println(get1);

        System.out.println("--------分割线---------");

        //key(*)
        Set<String> keyset = jedis.keys("*");
        for (String key:keyset){

            System.out.println(key);
        }
        System.out.println("--------分割线---------");

        //hashMap
        Map<String,String> userMap = jedis.hgetAll("user001");
        for (Map.Entry<String,String> entry :userMap.entrySet()){
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }

        System.out.println("--------分割线---------");
        //zset
        Set<Tuple> topTuple = jedis.zrevrangeWithScores("article:topn", 0, 2);
        for (Tuple tuple : topTuple) {
            System.out.println(tuple.getElement() + ":" + tuple.getScore());
        }

        System.out.println(jedis.ping());

        jedis.close();
        //自动检查要关闭的连接资源是来自哪里的，如果是new的就直接关闭，如果是池子借的就还回去。

    }
    //连接池
    private static JedisPool jedisPool = null;

    private static Jedis getJedis(){
      if (jedisPool==null){
          JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
          //池中总连接数
          jedisPoolConfig.setMaxTotal(100);
          //最小空闲连接数，
          jedisPoolConfig.setMinIdle(20);
          //最大空闲数。
          jedisPoolConfig.setMaxIdle(30);
          //是否等待
          jedisPoolConfig.setBlockWhenExhausted(true);
          //等待时间
          jedisPoolConfig.setMaxWaitMillis(5000);
          //导致无效连接的原因：
          //1：服务器重启 2：断过网重连 3：服务器端维持空闲时间超时
          //借出时是否测试，保证连接是有效的，不是无效连接
          jedisPoolConfig.setTestOnBorrow(true);
          //空闲时，偶尔进行连接自检，可加，比较保险
         // jedisPoolConfig.setTestWhileIdle(true);


          jedisPool = new JedisPool("hadoop102",6379);

      }
        Jedis jedisPoolResource = jedisPool.getResource();
         return jedisPoolResource;
    }


}
