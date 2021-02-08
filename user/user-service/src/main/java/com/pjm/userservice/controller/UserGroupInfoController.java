package com.pjm.userservice.controller;


import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.userservice.entity.UserGroupInfo;
import com.pjm.userservice.entityExt.UserExt;
import com.pjm.userservice.entityExt.UserGroupInfoExt;
import com.pjm.userservice.service.IUserGroupInfoService;
import com.pjm.userservice.util.CommonUtil;
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
@Api(tags = {"群聊信息控制器"})
@RestController
@RequestMapping("/userGroupInfo")
public class UserGroupInfoController {
    @Autowired
    private IUserGroupInfoService userGroupInfoService;

    @ApiOperation("添加一个群聊")
    @PostMapping("insert")
    public ResponseEntity<String> insertGroup(@RequestBody UserGroupInfo userGroupInfo) {
        return userGroupInfoService.insertGroup(userGroupInfo);
    }

    @ApiOperation("修改一个群聊")
    @PostMapping("update")
    public ResponseEntity<String> updateGroup(@RequestBody UserGroupInfo userGroupInfo) {
        return userGroupInfoService.updateGroup(userGroupInfo);
    }

    @ApiOperation("删除一个群聊")
    @GetMapping("delete/{id}")
    public ResponseEntity<String> deleteGroup(@PathVariable("id") String id) {
        return userGroupInfoService.deleteGroup(id);
    }

    @ApiOperation("自己的群聊列表")
    @PostMapping("listGroup")
    public ResponseEntity<PageVo<List<UserGroupInfo>>> listGroup(@RequestBody UserGroupInfoExt userGroupInfoExt) {
        return userGroupInfoService.listGroup(userGroupInfoExt, userGroupInfoExt.getPageNum(), userGroupInfoExt.getPageSize());
    }

    @ApiOperation("关键字检索群聊列表")
    @GetMapping("findGroupList/{searchKey}")
    public ResponseEntity<List<UserGroupInfo>> findGroupList(@PathVariable("searchKey")String searchKey){
        return ResponseEntity.success(userGroupInfoService.findGroupList(searchKey));
    }
}

