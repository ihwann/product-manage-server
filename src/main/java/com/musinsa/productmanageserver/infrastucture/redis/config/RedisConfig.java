package com.musinsa.productmanageserver.infrastucture.redis.config;


import com.musinsa.productmanageserver.properties.RedisProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private RedisServer redisServer;

    private final RedisProperties redisProperties;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.setCodec(new StringCodec());
        config.useSingleServer()
            .setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort());

        return Redisson.create(config);
    }

    @PostConstruct
    public void initEmbeddedRedis() {
        int port = Integer.parseInt(redisProperties.getPort());

        redisServer = new RedisServer(port);

        redisServer.start();
    }

    @PreDestroy
    public void destroyEmbeddedRedis() {
        redisServer.stop();
    }
}
