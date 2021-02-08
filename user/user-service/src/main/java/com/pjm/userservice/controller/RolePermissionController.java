package com.pjm.userservice.controller;


import com.pjm.common.entity.ResponseEntity;
import com.pjm.userservice.entity.RolePermission;
import com.pjm.userservice.entityExt.RolePermissionExt;
import com.pjm.userservice.service.IRolePermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author pjm
 * @since 2020-05-14
 */
@Api(tags = {"角色-权限"})
@RestController
@RequestMapping("/rolePermission")
public class RolePermissionController {
    @Autowired
    private IRolePermissionService rolePermissionService;

//    @ApiOperation("插入")
//    @PostMapping("/insert")
//    @RequiresPermissions(logical = Logical.AND, value = {"rolePermission/insert"})
//    public ResponseEntity<RolePermission> insert(@RequestBody RolePermission rolePermission) {
//        if (rolePermissionService.insert(rolePermission)) {
//            return new ResponseEntity<>(rolePermission);
//        } else {
//            return new ResponseEntity<>(500, "出错了");
//        }
//    }
//
//    @ApiOperation("修改")
//    @PostMapping("/edit")
//    @RequiresPermissions(logical = Logical.AND, value = {"rolePermission/edit"})
//    public ResponseEntity<RolePermission> edit(@RequestBody RolePermission rolePermission) {
//        if (rolePermissionService.updateById(rolePermission)) {
//            return new ResponseEntity<>(rolePermission);
//        } else {
//            return new ResponseEntity<>(500, "出错了");
//        }
//    }

    @ApiOperation("分配权限")
    @PostMapping("/assignPermissions")
    public ResponseEntity<String> assignPermissions(@RequestBody RolePermissionExt rolePermissionExt) {
        return rolePermissionService.assignPermissions(rolePermissionExt);
    }
    @ApiOperation("根据角色获取权限")
    @PostMapping("/getPermissionByRole")
    public ResponseEntity<List<String>> getPermissionByRole(@RequestBody RolePermission rolePermission) {
        return new ResponseEntity<>(rolePermissionService.getPermission(rolePermission));
    }
}

