package com.pjm.nettyservice.socket.filter;

import com.pjm.common.exception.CustomException;
import com.pjm.common.util.JwtUtil;
import com.pjm.common.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class GatewayLoginFilter {
    @Autowired
    private UserUtil userUtil;
    @Autowired
    private JwtUtil jwtUtil;

    public void doFilter(String token) {
        //验证登录状态
        if (StringUtils.isEmpty(token)) {
            throw new CustomException("无效密钥，必填");
        }
        if (!jwtUtil.verify((String) token)) {
            throw new CustomException("无效密钥，不合法");
        }
        userUtil.doLogin((String) token, null);
    }
}
