package com.pjm.userservice.controller;


import com.pjm.common.entity.ResponseEntity;
import com.pjm.userservice.entity.User;
import com.pjm.userservice.entity.UserGroupMemberInfo;
import com.pjm.userservice.entityExt.UserGroupMemberInfoExt;
import com.pjm.userservice.service.IUserGroupMemberInfoService;
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
@Api(tags = {"群成员信息控制器"})
@RestController
@RequestMapping("/userGroupMemberInfo")
public class UserGroupMemberInfoController {
    @Autowired
    private IUserGroupMemberInfoService userGroupMemberInfoService;

    @ApiOperation("申请加入指定群聊")
    @PostMapping("applicationJoinGroup")
    public ResponseEntity<String> applicationJoinGroup(@RequestBody UserGroupMemberInfo userGroupMemberInfo) throws InterruptedException {
        return userGroupMemberInfoService.applicationJoinGroup(userGroupMemberInfo);
    }

    @ApiOperation("同意加入指定群聊")
    @PostMapping("agree2JoinGroup")
    public ResponseEntity<String> agree2JoinGroup(@RequestBody UserGroupMemberInfo userGroupMemberInfo) {
        return userGroupMemberInfoService.agree2JoinGroup(userGroupMemberInfo);
    }

    @ApiOperation("删除指定群聊中的指定群员（支持批量）")
    @PostMapping("removeFromGroup")
    public ResponseEntity<String> removeFromGroup(@RequestBody UserGroupMemberInfoExt userGroupMemberInfoExt) {
        return userGroupMemberInfoService.removeFromGroup(userGroupMemberInfoExt);
    }

    @ApiOperation("根据群id获取群员列表")
    @GetMapping("getUserListByGroupId/{id}")
    public ResponseEntity<List<UserGroupMemberInfoExt>> getUserListByGroupId(@PathVariable("id") String id) {
        return userGroupMemberInfoService.getUserListByGroupId(id);
    }

    @ApiOperation("获取与我相关的入群申请审核列表")
    @GetMapping("getTheReviewListOfGroupMembershipApplicationRelatedToMe")
    public ResponseEntity<List<UserGroupMemberInfoExt>> getTheReviewListOfGroupMembershipApplicationRelatedToMe(){
        return ResponseEntity.success(userGroupMemberInfoService.getTheReviewListOfGroupMembershipApplicationRelatedToMe());
    }
}

