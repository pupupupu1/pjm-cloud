package com.pjm.userservice.controller;


import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.userservice.entity.UserFriendShip;
import com.pjm.userservice.entityExt.UserFriendShipExt;
import com.pjm.userservice.service.IUserFriendShipService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author pjm
 * @since 2020-11-08
 */
@CrossOrigin
@Api(tags = {"好友关系控制器"})
@RestController
@RequestMapping("/userFriendShip")
public class UserFriendShipController {
    @Autowired
    private IUserFriendShipService userFriendShipService;

    @ApiOperation("申请添加好友")
    @PostMapping("request2AddFriends")
    public ResponseEntity<String> d(@RequestBody UserFriendShip userFriendShip) {
        return userFriendShipService.request2AddFriends(userFriendShip);
    }

    @ApiOperation("单方面修改好友状态")
    @PostMapping("updateFriendStatus")
    public ResponseEntity<String> updateFriendStatus(@RequestBody UserFriendShip userFriendShip) {
        return userFriendShipService.updateFriendStatus(userFriendShip);
    }

    @ApiOperation("删除好友")
    @PostMapping("deleteFriend")
    public ResponseEntity<String> deleteFriend(@RequestBody UserFriendShip userFriendShip) {
        return userFriendShipService.deleteFriend(userFriendShip);
    }

    @ApiOperation("好友列表")
    @PostMapping("friendList")
    public ResponseEntity<PageVo<List<UserFriendShipExt>>> friendList(@RequestBody UserFriendShipExt userFriendShipExt) {
        return userFriendShipService.friendList(userFriendShipExt, userFriendShipExt.getPageNum(), userFriendShipExt.getPageSize());
    }

    @ApiOperation("好友申请列表")
    @GetMapping("applyList")
    public ResponseEntity<List<UserFriendShipExt>> applyList() {
        return userFriendShipService.applyList();
    }

}

