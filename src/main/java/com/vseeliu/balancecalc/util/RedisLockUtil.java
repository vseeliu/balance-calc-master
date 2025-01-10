/*
 * Copyright (c) 2025- vseeliu. All rights reserved.
 *
 *  This file is part of the balance calculation. Without authorization, copying, modifying, distributing,
 *  or using part or all of the content of this file is prohibited,
 *  unless the following conditions are met:
 *  This copyright notice and other copyright information in the file must be retained.
 *  This copyright notice must be included in any modified or derivative works.
 *  This software is provided "as is" and does not provide any express or implied warranties,
 *  including but not limited to merchantability,
 *  fitness for a particular purpose, and non-infringement warranties. Under any circumstances,
 *  [company name or individual name] will not be responsible for any direct, indirect, incidental, special, exemplary,
 *  or consequential damages resulting from the use of this software,
 *  even if it has been informed that such damages may occur.
 *  For more information, please contact:：381054972@qq.com vseeliu
 */

package com.vseeliu.balancecalc.util;

import com.vseeliu.balancecalc.constant.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Distributed Lock Util
 *
 * @author vseeliu
 * @version 1.0
 * @since 1.0
 */
@Component
public class RedisLockUtil {
    private final DefaultRedisScript<Long> tryLockScript =
        new DefaultRedisScript<>(Constant.RedisLockConstant.TRY_LOCK_SCRIPT, Long.class);
    private final DefaultRedisScript<Long> releaseLockScript =
        new DefaultRedisScript<>(Constant.RedisLockConstant.RELEASE_LOCK_SCRIPT, Long.class);
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * Try to acquire a distributed lock.
     *
     * @param lockKey    The key of the lock.
     * @param uuid       The unique identifier of the lock, usually a UUID.
     * @param expireTime The expiration time of the lock (milliseconds).
     * @return Whether the lock was acquired successfully.
     */
    public boolean tryLock(String lockKey, String uuid, long expireTime) {
        String fullLockKey = Constant.RedisLockConstant.LOCK_PREFIX + lockKey;
        Long result = redisTemplate.execute(tryLockScript, Collections.singletonList(fullLockKey), uuid,
            String.valueOf(expireTime));
        return result == 1L;
    }

    /**
     * Release a distributed lock
     *
     * @param lockKey The key of the lock.
     * @param uuid    The unique identifier of the lock, usually a UUID.
     */
    public void releaseLock(String lockKey, String uuid) {
        String fullLockKey = Constant.RedisLockConstant.LOCK_PREFIX + lockKey;
        redisTemplate.execute(releaseLockScript, Collections.singletonList(fullLockKey), uuid);
    }
}