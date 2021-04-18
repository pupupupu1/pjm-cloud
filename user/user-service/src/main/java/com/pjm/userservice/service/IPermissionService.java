package com.pjm.userservice.service;

import com.baomidou.mybatisplus.service.IService;
import com.pjm.common.entity.PageVo;
import com.pjm.userservice.entity.Permission;
import com.pjm.userservice.entityExt.PermissionExt;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author pjm
 * @since 2020-05-14
 */
public interface IPermissionService extends IService<Permission> {

    PageVo<List<PermissionExt>> selectwithpagewithchildren(PermissionExt permissionExt, Integer pageNum, Integer pageSize);

    boolean delete(Permission permission);

    /**
     * 权限列表转树形结构
     *
     * @param permissionExtList 需要找出下级的权限
     * @param permissionExtList 权限列表
     * @return
     */
    List<PermissionExt> convertMenuTree(List<PermissionExt> permissionExtList);

    List<String> listByRoleId(String userId);
}
