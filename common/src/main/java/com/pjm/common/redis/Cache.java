package com.pjm.common.redis;

import com.pjm.common.common.Constant;
import com.pjm.common.util.JedisUtil;
import com.pjm.common.util.JwtUtil;
import com.pjm.common.util.common.PropertiesUtil;
import com.pjm.common.util.common.SerializableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Cache {
    @Autowired
    private JedisUtil jedisUtil;
    @Autowired
    private JwtUtil jwtUtil;
    /**
     * 缓存的key名称获取为cloud:cache:account
     * @param key
     */
    public String getKey(Object key) {
        return Constant.PREFIX_SHIRO_CACHE + jwtUtil.getClaim(key.toString(), Constant.ACCOUNT);
    }

    /**
     * 获取缓存
     */

    public Object get(Object key)  {
        if(Boolean.FALSE.equals(jedisUtil.exists(this.getKey(key)))){
            return null;
        }
        return jedisUtil.getObject(this.getKey(key));
    }

    /**
     * 保存缓存
     */
    public Object put(Object key, Object value) {
        // 读取配置文件，获取Redis的Shiro缓存过期时间
        PropertiesUtil.readProperties("config.properties");
        String shiroCacheExpireTime = PropertiesUtil.getProperty("shiroCacheExpireTime");
        // 设置Redis的Shiro缓存
        return jedisUtil.setObject(this.getKey(key), value, Integer.parseInt(shiroCacheExpireTime));
    }

    /**
     * 移除缓存
     */

    public Object remove(Object key) {
        if(Boolean.FALSE.equals(jedisUtil.exists(this.getKey(key)))){
            return null;
        }
        jedisUtil.delKey(this.getKey(key));
        return null;
    }

    /**
     * 清空所有缓存
     */

    public void clear() {
        Objects.requireNonNull(jedisUtil.getJedis()).flushDB();
    }

    /**
     * 缓存的个数
     */

    public int size() {
        Long size = Objects.requireNonNull(jedisUtil.getJedis()).dbSize();
        return size.intValue();
    }

    /**
     * 获取所有的key
     */

    public Set keys() {
        Set<byte[]> keys = Objects.requireNonNull(jedisUtil.getJedis()).keys("*".getBytes());
        Set<Object> set = new HashSet<Object>();
        for (byte[] bs : keys) {
            set.add(SerializableUtil.unserializable(bs));
        }
        return set;
    }

    /**
     * 获取所有的value
     */

    public Collection values() {
        Set keys = this.keys();
        List<Object> values = new ArrayList<Object>();
        for (Object key : keys) {
            values.add(jedisUtil.getObject(this.getKey(key)));
        }
        return values;
    }
}
