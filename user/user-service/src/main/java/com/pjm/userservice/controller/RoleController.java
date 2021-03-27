package com.pjm.userservice.controller;


import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.common.util.common.UuidUtil;
import com.pjm.userservice.entity.Role;
import com.pjm.userservice.entityExt.RoleExt;
import com.pjm.userservice.service.IRoleService;
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
@Api(tags = {"角色"})
@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private IRoleService roleService;

    @ApiOperation("插入")
    @PostMapping("/insert")
    public ResponseEntity<Role> insert(@RequestBody Role role) {
        role.setId(UuidUtil.next());
        if (roleService.insert(role)) {
            return new ResponseEntity<>(role);
        } else {
            return new ResponseEntity<>(500, "出错了");
        }
    }

    @ApiOperation("修改")
    @PostMapping("/edit")

    public ResponseEntity<Role> edit(@RequestBody Role role) {
        if (roleService.updateById(role)) {
            return new ResponseEntity<>(role);
        } else {
            return new ResponseEntity<>(500, "出错了");
        }
    }

    @ApiOperation("删除")
    @PostMapping("/delete")

    public ResponseEntity<Role> delete(@RequestBody Role role) {
        if (roleService.deleteById(role.getId())) {
            return new ResponseEntity<>(role);
        } else {
            return new ResponseEntity<>(500, "出错了");
        }
    }

    @ApiOperation("角色列表")
    @PostMapping("/list")

    public ResponseEntity<PageVo<List<Role>>> listRole(@RequestBody RoleExt roleExt) {
        return new ResponseEntity<>(roleService.listWithPage(roleExt, roleExt.getPageNum(), roleExt.getPageSize()));
    }


}

