package org.secutity.auth.token.storage;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @Author AlanMa
 */
public class RedisJwtTokenStorage extends ExpireTokenStorage {

    private StringRedisTemplate redisTemplate;

    public RedisJwtTokenStorage(StringRedisTemplate redisTemplate) {
        this(redisTemplate, DEFAULT_EXPIRE_MINUTES);
    }

    public RedisJwtTokenStorage (StringRedisTemplate redisTemplate, Long expireMinutes) {
        super(expireMinutes);
        this.redisTemplate = redisTemplate;
    }

    /**
     * 保存token到redis中，并设置token的有效期
     * @param token token值
     */
    @Override
    public void saveToken(String token) {
        redisTemplate.opsForValue().set(getTokenKey(token), token, getExpireMinutes(), TimeUnit.MINUTES);
    }

    @Override
    public boolean exists(String token) {
        return redisTemplate.opsForValue().get(getTokenKey(token)) != null;
    }

    @Override
    public boolean resetExpireTime(String token) {
        return redisTemplate.expire(getTokenKey(token), getExpireMinutes(), TimeUnit.MINUTES);
    }

    @Override
    public long getTokenTTL(String token) {
        return redisTemplate.getExpire(getTokenKey(token));
    }
}
