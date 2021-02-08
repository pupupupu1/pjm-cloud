package com.pjm.userservice.api;


import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
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
@Api(tags = {"角色ApiClient"})
@RestController
@RequestMapping("/roleApiClient")
public class RoleApiController {
    @Autowired
    private IRoleService roleService;

    @ApiOperation("角色列表")
    @PostMapping("/selectList")
    public ResponseEntity<PageVo<List<Role>>> selectList(@RequestBody RoleExt roleExt) {
        return new ResponseEntity<>(roleService.listWithPage(roleExt, roleExt.getPageNum(), roleExt.getPageSize()));
    }


}

