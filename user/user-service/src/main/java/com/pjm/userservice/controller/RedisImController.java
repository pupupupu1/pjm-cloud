package com.pjm.userservice.controller;

import com.pjm.common.entity.ResponseEntity;
import com.pjm.common.util.JedisUtil;
import com.pjm.common.util.JwtUtil;
import com.pjm.common.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("im")
public class RedisImController {
    @Autowired
    private JedisUtil jedisUtil;

    @Autowired
    private UserUtil userUtil;
    @Autowired
    private HttpServletRequest request;

    @GetMapping("/getOfflineMsgList")
    public ResponseEntity<List<String>> getList() {
        String account = userUtil.getAccount(request);
        List<String> stringList = jedisUtil.popAll("pjm:im:receiveAccount:" + account);
        return ResponseEntity.success(stringList);
    }
}
