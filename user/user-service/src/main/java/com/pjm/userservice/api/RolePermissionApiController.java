package com.pjm.userservice.api;


import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.userservice.entity.Permission;
import com.pjm.userservice.entity.RolePermission;
import com.pjm.userservice.entityExt.PermissionExt;
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
@Api(tags = {"角色-权限ApiClient"})
@RestController
@RequestMapping("/rolePermissionApiClient")
public class RolePermissionApiController {
    @Autowired
    private IRolePermissionService rolePermissionService;

    @ApiOperation("根据角色获取权限")
    @PostMapping("/selectList")
    public ResponseEntity<PageVo<List<RolePermission>>> selectList(@RequestBody RolePermissionExt rolePermissionExt) {
        return new ResponseEntity<>(rolePermissionService.listWithPage(rolePermissionExt,rolePermissionExt.getPageNum(),rolePermissionExt.getPageSize()));
    }
}

