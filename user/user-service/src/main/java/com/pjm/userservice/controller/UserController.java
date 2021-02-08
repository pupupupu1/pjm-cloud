package com.pjm.userservice.controller;

import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.common.util.common.UuidUtil;
import com.pjm.userservice.entity.User;
import com.pjm.userservice.entityExt.UserExt;
import com.pjm.userservice.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author pjm
 * @since 2020-05-14
 */
@CrossOrigin
@Api(tags = {"用户"})
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;

    /**
     * 登录授权
     *
     * @param user
     */
    @ApiOperation("登录")
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        return userService.loginUser(user);
    }

    /**
     * 新增用户
     *
     * @param user
     */
    @ApiOperation("插入")
    @PostMapping("/insert")

    public ResponseEntity<User> insert(@RequestBody User user) {
        user.setId(UuidUtil.next());
        return userService.insertUser(user);
    }

    @ApiOperation("修改")
    @PostMapping("/edit")

    public ResponseEntity<User> edit(@RequestBody User user) {
        if (userService.updateById(user)) {
            return new ResponseEntity<>(user);
        } else {
            return new ResponseEntity<>(500, "出错了");
        }
    }

    @ApiOperation("删除")
    @PostMapping("/delete")

    public ResponseEntity<User> delete(@RequestBody User user) {
        if (userService.deleteById(user.getId())) {
            return new ResponseEntity<>(user);
        } else {
            return new ResponseEntity<>(500, "出错了");
        }
    }

    @ApiOperation("用户列表")
    @PostMapping("/list")

    public ResponseEntity<PageVo<List<User>>> listUser(@RequestBody UserExt userExt) {
        return new ResponseEntity<>(userService.listWithPage(userExt, userExt.getPageNum(), userExt.getPageSize()));
    }

    @ApiOperation("查询用户")
    @GetMapping("/searchList/{searchKey}")
    public ResponseEntity<List<User>> searchList(@PathVariable("searchKey") String searchkey) {
        return ResponseEntity.success(userService.searchList(searchkey));
    }

    @ApiOperation("信息初始化")
    @PostMapping("/init")

    public ResponseEntity<UserExt> initUser() {
        return userService.initUser();
    }

    @ApiOperation("用户信息详情")
    @PostMapping("/details")
    public ResponseEntity<UserExt> detailsUser(@RequestBody User user) {
        return ResponseEntity.success(userService.detailsUser(user));
    }

    @ApiOperation("退出")
    @PostMapping("/logout")

    public ResponseEntity<Boolean> logout(@RequestBody User user) {
        return new ResponseEntity<>(userService.logout(user));
    }

    @ApiOperation("修改密码")
    @PostMapping("/updatePassword")

    public ResponseEntity<User> updatePassword(@RequestBody User user) {
        return new ResponseEntity<>(userService.updatePassword(user));
    }

    @ApiOperation("重置密码")
    @PostMapping("/resetPassword")

    public ResponseEntity<User> resetPassword(@RequestBody User user) {
        return new ResponseEntity<>(userService.ResetPassword(user));
    }


}

