package com.pjm.userservice.util;

import com.alibaba.fastjson.JSON;
import com.pjm.common.exception.CustomException;
import com.pjm.common.exception.LoginException;
import com.pjm.common.redis.Cache;
import com.pjm.common.util.UserUtil;
import com.pjm.userapi.entity.ext.UserApiExt;
import com.pjm.userservice.entityExt.UserExt;
import com.pjm.userservice.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Component
public class CommonUtil {
    @Autowired
    private UserUtil userUtil;
    @Autowired
    private Cache cache;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private IUserService userService;
    public UserExt getUserInfo() {
        //此处进行缓存查询
        String token = userUtil.getToken(request);
        UserApiExt subject = (UserApiExt) cache.get(token);
        if (Objects.isNull(subject)) {
            //如果用户信息没有，就从数据库加载
            com.pjm.userservice.entityExt.UserExt userExt = userService.initUser(userUtil.getAccount(request)).getData();
            if (Objects.isNull(userExt)) {
                throw new LoginException(500,"无效用户");
            }
            subject = JSON.parseObject(JSON.toJSONString(userExt), UserApiExt.class);
            //同步进redis
            cache.put(userUtil.getToken(request), subject);
            return userExt;
        } else {
            return JSON.parseObject(JSON.toJSONString(subject), UserExt.class);
        }
    }
}
