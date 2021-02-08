package com.pjm.userservice.controller;


import com.pjm.common.entity.ResponseEntity;
import com.pjm.userservice.entityExt.UserRoleExt;
import com.pjm.userservice.service.IUserRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author pjm
 * @since 2020-05-14
 */
@Api(tags = {"用户-角色"})
@RestController
@RequestMapping("/userRole")
public class UserRoleController {
    @Autowired
    private IUserRoleService userRoleService;

//    @ApiOperation("插入")
//    @PostMapping("/insert")
//    @RequiresPermissions("userRole/insert")
//    public ResponseEntity<UserRole> insert(@RequestBody UserRole userRole) {
//        if (userRoleService.insert(userRole)) {
//            return new ResponseEntity<>(userRole);
//        } else {
//            return new ResponseEntity<>(500, "出错了");
//        }
//    }
//
//    @ApiOperation("修改")
//    @PostMapping("/edit")
//    @RequiresPermissions("userRole/edit")
//    public ResponseEntity<UserRole> edit(@RequestBody UserRole userRole) {
//        if (userRoleService.updateById(userRole)) {
//            return new ResponseEntity<>(userRole);
//        } else {
//            return new ResponseEntity<>(500, "出错了");
//        }
//    }

    @ApiOperation("分配角色")
    @PostMapping("/assigningRoles")
    public ResponseEntity<String> assigningRoles(@RequestBody UserRoleExt userRoleExt) {
        return userRoleService.assigningRoles(userRoleExt);
    }
}

