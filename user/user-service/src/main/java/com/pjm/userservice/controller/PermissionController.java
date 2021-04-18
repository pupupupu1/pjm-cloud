package com.pjm.userservice.controller;


import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.common.util.common.UuidUtil;
import com.pjm.userservice.entity.Permission;
import com.pjm.userservice.entityExt.PermissionExt;
import com.pjm.userservice.service.IPermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author pjm
 * @since 2020-05-14
 */
@Api(tags = {"权限"})
@RestController
@RequestMapping("/permission")
public class PermissionController {
    @Autowired
    private IPermissionService permissionService;

    @ApiOperation("插入")
    @PostMapping("/insert")
    public ResponseEntity<Permission> insert(@RequestBody Permission permission) {
        permission.setId(UuidUtil.next());
        if (permissionService.insert(permission)) {
            return new ResponseEntity<>(permission);
        } else {
            return new ResponseEntity<>(500, "出错了");
        }
    }

    @ApiOperation("修改")
    @PostMapping("/edit")
    public ResponseEntity<Permission> edit(@RequestBody Permission permission) {
        if (permissionService.updateById(permission)) {
            return new ResponseEntity<>(permission);
        } else {
            return new ResponseEntity<>(500, "出错了");
        }
    }

    @ApiOperation("删除")
    @PostMapping("/delete")
    public ResponseEntity<Permission> delete(@RequestBody Permission permission) {
        if (permissionService.delete(permission)) {
            return new ResponseEntity<>(permission);
        } else {
            return new ResponseEntity<>(500, "出错了");
        }
    }

    @ApiOperation("树形列表")
    @PostMapping("/ListWithPageWithChildren")
    public ResponseEntity<PageVo<List<PermissionExt>>> listWithPageWithChildren(@RequestBody PermissionExt permissionExt) {
        return new ResponseEntity<>(permissionService.selectwithpagewithchildren(permissionExt, permissionExt.getPageNum(), permissionExt.getPageSize()));
    }

    @ApiOperation("获取已有权限")
    @GetMapping("/listByRoleId/{roleId}")
    public ResponseEntity<List<String>> listByUserId(@PathVariable("roleId") String roleId) {
        return new ResponseEntity<>(permissionService.listByRoleId(roleId));
    }
}

