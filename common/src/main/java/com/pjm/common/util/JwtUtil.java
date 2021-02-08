package com.pjm.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.pjm.common.common.Constant;
import com.pjm.common.exception.CustomException;
import com.pjm.common.redis.Cache;
import com.pjm.common.util.common.Base64ConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * JAVA-JWT工具类
 */
@Slf4j
@Component
public class JwtUtil {
    @Autowired
    private JedisUtil jedisUtil;
    /**
     * 过期时间改为从配置文件获取
     */
    @Value("${accessTokenExpireTime}")
    private String accessTokenExpireTime;

    /**
     * JWT认证加密私钥(Base64加密)
     */
    @Value("${encryptJWTKey}")
    private String encryptJWTKey;

//    @Value("${accessTokenExpireTime}")
//    public void setAccessTokenExpireTime(String accessTokenExpireTime) {
//        this.accessTokenExpireTime = accessTokenExpireTime;
//    }
//
//    @Value("${encryptJWTKey}")
//    public void setEncryptJWTKey(String encryptJWTKey) {
//        this.encryptJWTKey = encryptJWTKey;
//    }

    /**
     * 校验token是否正确
     *
     * @param token Token
     * @return boolean 是否正确
     */
    public boolean verify(String token) {
        try {
            // 帐号加JWT私钥解密
            String secret = getClaim(token, Constant.ACCOUNT) + Base64ConvertUtil.decode(encryptJWTKey);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (UnsupportedEncodingException e) {
            log.error("JWTToken认证解密出现UnsupportedEncodingException异常:{}", e.getMessage());
            throw new CustomException("JWTToken认证解密出现UnsupportedEncodingException异常:" + e.getMessage());
        }
    }

    /**
     * 获得Token中的信息无需secret解密也能获得
     *
     * @param token
     * @param claim
     * @return java.lang.String
     */
    public String getClaim(String token, String claim) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            // 只能输出String类型，如果是其他类型返回null
            return jwt.getClaim(claim).asString();
        } catch (JWTDecodeException e) {
            log.error("解密Token中的公共信息出现JWTDecodeException异常:{}", e.getMessage());
            throw new CustomException("解密Token中的公共信息出现JWTDecodeException异常:" + e.getMessage());
        }
    }

    /**
     * 生成签名
     *
     * @param account 帐号
     * @return java.lang.String 返回加密的Token
     */
    public String sign(String account, String currentTimeMillis) {
        try {
            jedisUtil.setObject(Constant.PREFIX_SHIRO_ACCESS_TOKEN+account,currentTimeMillis);
            // 帐号加JWT私钥加密
            String secret = account + Base64ConvertUtil.decode(encryptJWTKey);
            System.out.println(secret);
            // 此处过期时间是以毫秒为单位，所以乘以1000
//            Date date = new Date(System.currentTimeMillis() + 1000);
            Date date = new Date(System.currentTimeMillis() + Long.parseLong(accessTokenExpireTime) * 1000);
            System.out.println(date);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            System.out.println(algorithm);
            // 附带account帐号信息（设置jwt不过期）
            return JWT.create()
                    .withClaim(Constant.ACCOUNT, account)
                    .withClaim(Constant.CURRENT_TIME_MILLIS, currentTimeMillis)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            log.error("JWTToken加密出现UnsupportedEncodingException异常:{}", e.getMessage());
            throw new CustomException("JWTToken加密出现UnsupportedEncodingException异常:" + e.getMessage());
        }
    }
}
