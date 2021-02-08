package com.pjm.userservice.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.common.util.common.UuidUtil;
import com.pjm.userservice.entity.Permission;
import com.pjm.userservice.entity.Role;
import com.pjm.userservice.entity.RolePermission;
import com.pjm.userservice.entityExt.RolePermissionExt;
import com.pjm.userservice.mapper.RolePermissionMapper;
import com.pjm.userservice.service.IPermissionService;
import com.pjm.userservice.service.IRolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
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
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements IRolePermissionService {

    @Autowired
    private IPermissionService permissionService;

    @Override
    public List<RolePermission> finPermissionByRole(Role role) {
        return selectList(new EntityWrapper<>(new RolePermission()).andNew().eq("role_id", role.getId()));
    }

    @Override
    public PageVo<List<RolePermission>> listWithPage(RolePermissionExt rolePermissionExt, Integer pageNum, Integer pageSize) {
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        Wrapper<RolePermission> rolePermissionExtWrapper = new EntityWrapper<>(new RolePermission());
        if (!StringUtils.isEmpty(rolePermissionExt.getId())) {
            rolePermissionExtWrapper.andNew().eq("id", rolePermissionExt.getId());
        }
        if (!StringUtils.isEmpty(rolePermissionExt.getRoleId())) {
            rolePermissionExtWrapper.andNew().like("role_id", rolePermissionExt.getRoleId());
        }
        if (!CollectionUtils.isEmpty(rolePermissionExt.getRoleIds())) {
            rolePermissionExt.getRoleIds().add("89898989898");
            rolePermissionExtWrapper.andNew().in("role_id", rolePermissionExt.getRoleIds());
        }
        if (!StringUtils.isEmpty(rolePermissionExt.getPermissionId())) {
            rolePermissionExtWrapper.andNew().like("permission_id", rolePermissionExt.getPermissionId());
        }
        List<RolePermission> rolePermissionList = selectList(rolePermissionExtWrapper);
        PageInfo<RolePermission> pageInfo = new PageInfo<>(rolePermissionList);
        return new PageVo<>(pageNum, pageSize, pageInfo.getTotal(), rolePermissionList);
    }

    @Override
    public ResponseEntity<String> assignPermissions(RolePermissionExt rolePermissionExt) {
        List<String> permissionIdArray = rolePermissionExt.getPermissionIds();
        Wrapper<RolePermission> rolePermissionWrapper = new EntityWrapper<>(new RolePermission());
        rolePermissionWrapper.andNew().eq("role_id", rolePermissionExt.getRoleId());
        delete(rolePermissionWrapper);
        for (String permissionId : permissionIdArray) {
            if (StringUtils.isEmpty(permissionId)) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setId(UuidUtil.next());
                rolePermission.setRoleId(rolePermissionExt.getRoleId());
                rolePermission.setPermissionId(permissionId);
                insert(rolePermission);
            }
        }
        return new ResponseEntity<>("success");
    }

    @Override
    public List<String> getPermission(RolePermission rolePermission) {
        List<String> ids = new ArrayList<>();
        List<RolePermission> rolePermissionList = selectList(new EntityWrapper<>(new RolePermission()).andNew().eq("role_id", rolePermission.getRoleId()));
        for (RolePermission rolePermissiontemp : rolePermissionList) {
            Permission permission = permissionService.selectById(rolePermissiontemp.getPermissionId());
            if (permission != null) {
                ids.add(permission.getId());
            }
        }
        return ids;
    }
}
