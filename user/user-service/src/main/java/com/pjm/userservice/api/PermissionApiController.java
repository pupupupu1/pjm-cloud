package com.pjm.userservice.api;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.userservice.entity.Permission;
import com.pjm.userservice.entity.Role;
import com.pjm.userservice.entityExt.PermissionExt;
import com.pjm.userservice.service.IPermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author pjm
 * @since 2020-05-14
 */
@Api(tags = {"权限ApiClient"})
@RestController
@RequestMapping("/permissionApiClient")
public class PermissionApiController {
    @Autowired
    private IPermissionService permissionService;

    @ApiOperation("列表")
    @PostMapping("/selectList")
    public ResponseEntity<PageVo<List<Permission>>> listWithPageWithChildren(@RequestBody PermissionExt permissionExt) {
        if (permissionExt.getPageNum() > 0 && permissionExt.getPageSize() > 0) {
            PageHelper.startPage(permissionExt.getPageNum() , permissionExt.getPageSize() );
        }
        Wrapper<Permission> permissionWrapper = new EntityWrapper<>(new Permission());
        if (!StringUtils.isEmpty(permissionExt.getId())) {
            permissionWrapper.andNew().eq("id", permissionExt.getId());
        }
        if (!CollectionUtils.isEmpty(permissionExt.getIds())) {
            permissionExt.getIds().add("99999999");
            permissionWrapper.andNew().in("id", permissionExt.getIds());
        }
        if (!StringUtils.isEmpty(permissionExt.getPermissionCode())) {
            permissionWrapper.andNew().like("role_name", permissionExt.getPermissionCode());
        }
        List<Permission> permissionList = permissionService.selectList(permissionWrapper);
        PageInfo<Permission> pageInfo = new PageInfo<>(permissionList);
        return ResponseEntity.success(new PageVo<>(permissionExt.getPageNum(), permissionExt.getPageSize(), pageInfo.getTotal(), permissionList));
    }
}

