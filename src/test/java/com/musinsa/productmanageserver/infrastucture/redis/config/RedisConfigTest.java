package com.musinsa.productmanageserver.infrastucture.redis.config;

import jakarta.annotation.PreDestroy;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import redis.embedded.RedisServer;

@TestConfiguration
public class RedisConfigTest {

    private RedisServer redisServer;

    @Bean
    public RedisServer embeddedRedisServer(){
        int port = Integer.parseInt("6380");
        redisServer = new RedisServer(port);
        redisServer.start();
        return redisServer;
    }

    @Bean
    @DependsOn("embeddedRedisServer")
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.setCodec(new StringCodec());
        config.useSingleServer()
            .setAddress("redis://" + "127.0.0.1" + ":" + "6380");
        return Redisson.create(config);
    }

    @PreDestroy
    public void destroyEmbeddedRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}