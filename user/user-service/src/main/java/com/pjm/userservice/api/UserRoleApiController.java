package com.pjm.userservice.api;


import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.userservice.entity.UserRole;
import com.pjm.userservice.entityExt.UserRoleExt;
import com.pjm.userservice.service.IUserRoleService;
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
@Api(tags = {"用户-角色"})
@RestController
@RequestMapping("/userRole")
public class UserRoleApiController {
    @Autowired
    private IUserRoleService userRoleService;

    @ApiOperation("分配角色")
    @PostMapping("/selectList")
    public ResponseEntity<PageVo<List<UserRole>>> selectList(@RequestBody UserRoleExt userRoleExt) {
        return ResponseEntity.success(userRoleService.listWithPage(userRoleExt,userRoleExt.getPageNum(),userRoleExt.getPageSize()));
    }
}

