package com.pjm.common.util;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.pjm.common.common.Constant;
import com.pjm.common.exception.CustomException;
import com.pjm.common.exception.LoginException;
import com.pjm.common.util.common.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

/**
 * 获取当前登录用户工具类
 */
@Slf4j
@Component
@PropertySource("classpath:config.properties")
public class UserUtil {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JedisUtil jedisUtil;
    @Value("${refreshTokenExpireTime}")
    private String refreshTokenExpireTime;
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 获取当前登录用户Token
     *
     * @param
     */
    public String getToken(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst("Authorization");
    }

    public String getToken(HttpServletRequest request) {
        String res = request.getHeader("Authorization");
        if (StringUtils.isEmpty(res)) {
            throw new CustomException("未登录");
        }
        return res;
    }

    public String getAccount(String token) {
        return jwtUtil.getClaim(token, Constant.ACCOUNT);
    }

    /**
     * 获取当前登录用户Account
     *
     * @param
     */
    public String getAccount(ServerWebExchange exchange) {
        String token = getToken(exchange);
        // 解密获得Account
        return jwtUtil.getClaim(token, Constant.ACCOUNT);
    }

    public String getAccount(HttpServletRequest request) {
        String token = getToken(request);
        // 解密获得Account
        return jwtUtil.getClaim(token, Constant.ACCOUNT);
    }

    public String getReqApplicationName(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        String[] paths = path.split("/");
        log.info(paths[1]);
        return paths[1];
    }

    public String getInterFaceName(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        String[] paths = path.split("/");
        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < paths.length; i++) {
            sb.append(paths[i]).append("/");
        }
        log.info(sb.toString());
        return "/" + sb.toString().substring(0, sb.toString().length() - 1);
    }

    public void doLogin(ServerWebExchange exchange) {
        doLogin(getToken(exchange), exchange);
        //解析token获取username和redis里的对比
        String account = jwtUtil.getClaim(getToken(exchange), Constant.ACCOUNT);
        //判断token解密的时间戳和
        log.info("登录成功：{}", account);
    }

    public void doLogin(String token, ServerWebExchange exchange) {
        String account = null;
        //验证token合法性
        try {
            account = jwtUtil.getClaim(token, Constant.ACCOUNT);
            jwtUtil.verify(token);
            jedisUtil.expire(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account, Integer.parseInt(refreshTokenExpireTime));
        } catch (TokenExpiredException e) {
            log.error("本次请求的token已经过期,判断refreshToken{}，异常是{}", token, e.getMessage());
            refreshToken(token, exchange);
        }catch(Exception e){
            throw new CustomException("认证异常"+e.getMessage());
        }
    }

    public void refreshToken(String token, ServerWebExchange exchange) {
        String account = jwtUtil.getClaim(token, Constant.ACCOUNT);
        if (jedisUtil.exists(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account)) {
            log.info("refreshToken存在，刷新accesstoken");
            //判断refreshToken对应的时间戳和token解密的时间戳是否相同，不同的话就是被挤下去了
            String refreshTokenCurrTime = jedisUtil.getObject(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account).toString();
            if (jwtUtil.getClaim(token, Constant.CURRENT_TIME_MILLIS).equals(refreshTokenCurrTime)) {
                log.info("生成新token");
                //最新时间戳
                String systime = String.valueOf(System.currentTimeMillis());
                // 设置RefreshToken中的时间戳为当前最新时间戳，且刷新过期时间重新为30分钟过期(配置文件可配置refreshTokenExpireTime属性)
                jedisUtil.setObject(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account, systime, Integer.parseInt(refreshTokenExpireTime));
                // 刷新AccessToken，设置时间戳为当前最新时间戳
                token = jwtUtil.sign(account, systime);
                ServerHttpResponse response = exchange.getResponse();
                response.getHeaders().add("Authorization", token);
                response.getHeaders().add("Access-Control-Expose-Headers", "Authorization");
            } else {
                throw new LoginException(401, "登录过期");
            }
        } else {
            throw new LoginException(401, "登录过期");
        }
    }
}
