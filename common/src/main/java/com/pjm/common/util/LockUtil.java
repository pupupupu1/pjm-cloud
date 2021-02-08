package com.pjm.common.util;

import com.pjm.common.common.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 分布式锁工具类，基于jedis
 */
@Component
public class LockUtil {
    @Autowired
    private JedisUtil jedisUtil;

    public boolean setIfNotLock(String lockKey,Integer lockTime){
        if (jedisUtil.exists(Constant.PJM_CLOUD_LOCK+lockKey)){
            return false;
        }else {
            jedisUtil.setObject(Constant.PJM_CLOUD_LOCK+lockKey,System.currentTimeMillis(),lockTime);
            return true;
        }
    }
}
