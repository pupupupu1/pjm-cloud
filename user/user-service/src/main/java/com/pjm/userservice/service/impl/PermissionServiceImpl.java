package com.pjm.userservice.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pjm.common.entity.PageVo;
import com.pjm.userservice.entity.Permission;
import com.pjm.userservice.entityExt.PermissionExt;
import com.pjm.userservice.mapper.PermissionMapper;
import com.pjm.userservice.service.IPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author pjm
 * @since 2020-05-14
 */
@Transactional
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {
    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public PageVo<List<PermissionExt>> selectwithpagewithchildren(PermissionExt permissionExt, Integer pageNum, Integer pageSize) {
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<PermissionExt> permissionExtList = permissionMapper.selectWithPageWithChildren(permissionExt);
        PageInfo<PermissionExt> pageInfo = new PageInfo<>(permissionExtList);
        return new PageVo<>(pageNum, pageSize, pageInfo.getTotal(), permissionExtList);
    }

    @Override
    public boolean delete(Permission permission) {
        Wrapper<Permission> permissionWrapper = new EntityWrapper<>(new Permission());
        permissionWrapper.andNew().eq("parent_id", permission.getId() + "");
        List<Permission> permissionList = selectList(permissionWrapper);
        List<String> ids = new ArrayList<>();
        for (Permission permission1 : permissionList) {
            ids.add(permission1.getId() + "");
        }
        if (ids.size() > 0) {
            deleteBatchIds(ids);
        }
        deleteById(permission.getId());
        return true;
    }

    @Override
    public List<PermissionExt> convertMenuTree(List<PermissionExt> menuList) {
        List<PermissionExt> menuTree = new ArrayList<>();
        for (PermissionExt sysMenu : menuList) {
            if (StringUtils.isEmpty(sysMenu.getParentId())) {
                convertTree(sysMenu, menuList);
                menuTree.add(sysMenu);
            }
        }
        return menuTree;
    }

    private void convertTree(PermissionExt permission, List<PermissionExt> permissionExtList) {
        for (PermissionExt permissionExt : permissionExtList) {
            if (permission.getId().equals(permissionExt.getParentId())) {
                convertTree(permissionExt, permissionExtList);
                List<PermissionExt> children = permission.getChildren();
                if (children == null) {
                    permission.setChildren(new ArrayList<>());
                }
                permission.getChildren().add(permissionExt);
            }
        }
    }
}
