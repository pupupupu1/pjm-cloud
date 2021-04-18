package com.pjm.userservice.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pjm.common.entity.PageVo;
import com.pjm.userservice.entity.Role;
import com.pjm.userservice.entity.UserRole;
import com.pjm.userservice.entityExt.RoleExt;
import com.pjm.userservice.mapper.RoleMapper;
import com.pjm.userservice.service.IRoleService;
import com.pjm.userservice.service.IUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

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
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {


    @Override
    public PageVo<List<Role>> listWithPage(RoleExt roleExt, Integer pageNum, Integer pageSize) {
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        Wrapper<Role> roleWrapper = new EntityWrapper<>(new Role());
        if (!StringUtils.isEmpty(roleExt.getId())) {
            roleWrapper.andNew().eq("id", roleExt.getId() + "");
        }
        if (!CollectionUtils.isEmpty(roleExt.getIds())) {
            roleExt.getIds().add("999999");
            roleWrapper.andNew().in("id", roleExt.getIds());
        }
        if (!StringUtils.isEmpty(roleExt.getRoleName())) {
            roleWrapper.andNew().like("role_name", roleExt.getRoleName());
        }
        List<Role> roleList = selectList(roleWrapper);
        PageInfo<Role> pageInfo = new PageInfo<>(roleList);
        return new PageVo<>(pageNum, pageSize, pageInfo.getTotal(), roleList);
    }

    @Autowired
    private IUserRoleService userRoleService;
    @Override
    public PageVo<List<Role>> listByUserId(String userId) {
        Wrapper<UserRole> wrapper = new EntityWrapper<>(new UserRole().setUserId(userId));
        List<UserRole> userRoles = userRoleService.selectList(wrapper);
        List<String> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        return listWithPage(new RoleExt().setIds(roleIds),0,0);
    }
}
