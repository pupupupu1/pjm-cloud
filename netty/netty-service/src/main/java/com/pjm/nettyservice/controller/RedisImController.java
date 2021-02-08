package com.pjm.nettyservice.controller;

import com.pjm.common.entity.ResponseEntity;
import com.pjm.common.util.JedisUtil;
import com.pjm.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("im")
public class RedisImController {
    @Autowired
    private JedisUtil jedisUtil;

    @GetMapping("/getList")
    public ResponseEntity<List<String>> getList(String account) {
        List<String> stringList = jedisUtil.popAll("pjm:im:receiveAccount:" + account);
        return ResponseEntity.success(stringList);
    }

    /**
     * 有bug，token可能被盗窃伪造
     *
     * @param account
     * @param token
     * @return
     */
    @PostMapping("/auth")
    public void auth(String account, String token, HttpServletResponse response){
        log.info("通话请求到来：account是{}，token是{}", account, token);
        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(token)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
        String tokenTemp = (String) jedisUtil.getObject("pjm:im:talk:session:" + account);
        if (token.equals(tokenTemp)) {
            log.info("访问成功");
        } else {
            log.error("访问失败");
//            throw new Exception("auth forbidden");
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
    }
}
