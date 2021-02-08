package com.pjm.userservice.api;

import com.pjm.common.entity.ResponseEntity;
import com.pjm.userservice.entity.User;
import com.pjm.userservice.entityExt.UserExt;
import com.pjm.userservice.service.IUserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.rmi.runtime.Log;

@Slf4j
@Api(tags = {"用户模块提供的接口ApiClient"})
@RestController
@RequestMapping("/userApiClient")
public class UserApiController {
    @Autowired
    private IUserService userService;

    @PostMapping("/findUserByAccountOrTel")
    public ResponseEntity<User> findUserByAccountOrTel(@RequestBody User user) {
        return ResponseEntity.success(userService.findUserByAccountOrTel(user));
    }

    @GetMapping("service")
    public String service() {
        return "hello user-service";
    }

    @GetMapping("getUser")
    public User getUser() {
        log.info("get user!!!!!!!!!!!!!!!!!!");
        return new User().setUserName("pjm").setUserPassword("ASDasdasdasd");
    }

    @GetMapping("initInfo")
    public ResponseEntity<UserExt> initInfo(@RequestParam("account") String account) {
        return userService.initUser(account);
    }
}
