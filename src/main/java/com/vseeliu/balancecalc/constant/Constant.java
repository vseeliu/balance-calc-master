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

package com.vseeliu.balancecalc.constant;

public interface Constant {
    interface RedisLockConstant {
        String LOCK_PREFIX = "distributed_lock:";
        String TRY_LOCK_SCRIPT =
            "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then redis.call('pexpire', KEYS[1], ARGV[2]) return 1 else return 0 end";
        String RELEASE_LOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
    }
}
