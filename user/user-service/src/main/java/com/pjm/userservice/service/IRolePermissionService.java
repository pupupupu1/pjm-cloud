package com.pjm.userservice.service;

import com.baomidou.mybatisplus.service.IService;
import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.userservice.entity.Role;
import com.pjm.userservice.entity.RolePermission;
import com.pjm.userservice.entityExt.RolePermissionExt;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author pjm
 * @since 2020-05-14
 */
public interface IRolePermissionService extends IService<RolePermission> {
    List<RolePermission> finPermissionByRole(Role role);

    PageVo<List<RolePermission>> listWithPage(RolePermissionExt rolePermissionExt, Integer pageNum, Integer pageSize);

    ResponseEntity<String> assignPermissions(RolePermissionExt rolePermissionExt);
    List<String> getPermission(RolePermission rolePermission);
}
