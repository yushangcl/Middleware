package com.whh.middleware.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

/**
 * Created by huahui.wu on 2018/1/3.
 */
public class RedisTest {

    public static void main(String[] args) {
        //连接本地的 Redis 服务
        Long start = System.currentTimeMillis();
        JedisShardInfo info = new JedisShardInfo("123.207.119.211");
        info.setPassword("147258369..");
        Jedis jedis = new Jedis(info);
        Long mid = System.currentTimeMillis();
        System.out.println("连接成功，耗时：" + (mid - start) + "ms");
        //设置 redis 字符串数据
        String str = jedis.get("TEST");
        Long end = System.currentTimeMillis();
        System.out.println("连接成功，获取TEST,耗时：" + (end - mid) + "ms");

        System.out.println("key: TEST  value: " + str);
        // 获取存储的数据并输出
        System.out.println("redis 存储的字符串为: "+ jedis.get("runoobkey"));
    }
}
