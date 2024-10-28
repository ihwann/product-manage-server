package com.musinsa.productmanageserver.infrastucture.redis.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCommandComponent {

    private final RedissonClient redissonClient;

    /**
     * ScoredSortedSet 객체 반환
     *
     * @param key key
     * @return ScoredSortedSet 객체
     */
    public RScoredSortedSet<String> getScoredSortedSet(String key) {
        return redissonClient.getScoredSortedSet(key);
    }
}
