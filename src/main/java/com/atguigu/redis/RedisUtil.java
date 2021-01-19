package com.atguigu.redis;

import com.sun.org.apache.regexp.internal.REUtil;
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RedisUtil {

    public static void main(String[] args) {

       // Jedis jedis =RedisUtil.getJedis();
       Jedis jedis =RedisUtil.getJedisFormSentinel();
        jedis.set("k1000","v1000");
        Set<String> keyset = jedis.keys("*");
        for (String key : keyset) {
            System.out.println(key);
        }
        Map<String, String> userMap = jedis.hgetAll("user:0101");
        for (Map.Entry<String, String> entry : userMap.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }

        Set<Tuple> topTuple = jedis.zrevrangeWithScores("article:topn", 0, 2);
        for (Tuple tuple : topTuple) {
            System.out.println(tuple.getElement() + ":" + tuple.getScore());
        }

        System.out.println(jedis.ping());
        jedis.close();

    }

    private static  JedisPool jedisPool=null ;


    public static  Jedis getJedis(){

        if(jedisPool==null){
            JedisPoolConfig jedisPoolConfig=new JedisPoolConfig();
            jedisPoolConfig.setMaxTotal(100);
            jedisPoolConfig.setMinIdle(20);
            jedisPoolConfig.setMaxIdle(30);
            //资源耗尽时等待
            jedisPoolConfig.setBlockWhenExhausted(true);
            jedisPoolConfig.setMaxWaitMillis(5000);
            //从池中去连接后要进行测试
            //导致连接池中的连接坏掉： 1 服务器端重启过 2 网断过 3 服务器端维持空闲连接超时
            jedisPoolConfig.setTestOnBorrow(true);
             // jedisPoolConfig.setTestWhileIdle(true);
            jedisPool=new JedisPool("hdp1",6379 );
        }
        Jedis jedis = jedisPool.getResource();
        return jedis;

    }
   //  could not  get resource from pool
    //1 检查地址端口
    //2  检查bind 是否注掉了
    //3  检查连接池资源是否耗尽 ，jedis使用后 没有通过close 还给池子



    private static JedisSentinelPool jedisSentinelPool=null;

    public static Jedis getJedisFormSentinel(){
        if(jedisSentinelPool==null){
            //创建哨兵池
            Set<String> sentinels=new HashSet<>();
            sentinels.add("192.168.11.101:26379");

            JedisPoolConfig jedisPoolConfig=new JedisPoolConfig();
            jedisPoolConfig.setMaxTotal(100);
            jedisPoolConfig.setMinIdle(20);
            jedisPoolConfig.setMaxIdle(30);
            //资源耗尽时等待
            jedisPoolConfig.setBlockWhenExhausted(true);
            jedisPoolConfig.setMaxWaitMillis(5000);
            //从池中去连接后要进行测试
            //导致连接池中的连接坏掉： 1 服务器端重启过 2 网断过 3 服务器端维持空闲连接超时
            jedisPoolConfig.setTestOnBorrow(true);

            jedisSentinelPool = new JedisSentinelPool("mymaster",sentinels,jedisPoolConfig);
        }
        Jedis jedis = jedisSentinelPool.getResource();
        return  jedis;


    }



}
