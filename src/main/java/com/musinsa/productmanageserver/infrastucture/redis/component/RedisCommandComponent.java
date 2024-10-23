package com.musinsa.productmanageserver.infrastucture.redis.component;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCommandComponent {

    private final RedissonClient redissonClient;

    private static final String LOCK_POST_FIX = ":lock";

    /**
     * Redis Key 생성
     * @param prefix prefix
     * @param params params
     * @return Redis Key
     */
    public String generateRedisKey(String prefix, String... params) {
        StringBuilder sb = new StringBuilder(prefix);

        for (String param : params) {
            sb.append(":");
            sb.append(param);
        }

        return sb.toString();
    }

    /**
     * lock 획득을 시도한다.
     * @param key key
     * @param waitTime 대기시간
     * @param leaseTime 점유시간
     * @param timeUnit 시간단위
     * @return lock 획득 여부
     */
    public boolean tryLock(String key, int waitTime, int leaseTime, TimeUnit timeUnit) {
        boolean isLocked = false;

        try {
            RLock lock = getLock(key);
            isLocked = lock.tryLock(waitTime, leaseTime, timeUnit);
        } catch (InterruptedException e) {
            log.error("tryLock Interrupt occur key : {}, error message : {}", key, e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("tryLock error key : {}, error message : {}", key, e.getMessage());
        }

        return isLocked;
    }

    /**
     * lock 해제
     * @param key key
     */
    public void unLock(String key) {
        RLock lock = getLock(key);
        if (!ObjectUtils.isEmpty(lock) && lock.isLocked() && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * lock 객체 반환
     * @param key key
     * @return lock 객체
     */
    public RLock getLock(String key) {
        redissonClient.getScoredSortedSet()
        return redissonClient.getLock(key + LOCK_POST_FIX);
    }
}
