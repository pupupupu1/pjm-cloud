package com.pjm.userservice.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.common.util.common.UuidUtil;
import com.pjm.userservice.entity.User;
import com.pjm.userservice.entity.UserRole;
import com.pjm.userservice.entityExt.UserRoleExt;
import com.pjm.userservice.mapper.UserRoleMapper;
import com.pjm.userservice.service.IUserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author pjm
 * @since 2020-05-14
 */
@Transactional
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

    @Override
    public List<UserRole> getRoleByUser(User user) {
        return selectList(new EntityWrapper<>(new UserRole()).andNew().eq("user_id",user.getId()));
    }

    @Override
    public PageVo<List<UserRole>> listWithPage(UserRoleExt userRoleExt, Integer pageNum, Integer pageSize) {
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        Wrapper<UserRole> userRoleWrapper = new EntityWrapper<>(new UserRole());
        if (!StringUtils.isEmpty(userRoleExt.getId())) {
            userRoleWrapper.andNew().eq("id", userRoleExt.getId()+"");
        }
        if (!StringUtils.isEmpty(userRoleExt.getUserId())) {
            userRoleWrapper.andNew().like("user_id", userRoleExt.getUserId()+"");
        }
        if (!StringUtils.isEmpty(userRoleExt.getRoleId())) {
            userRoleWrapper.andNew().like("role_id", userRoleExt.getRoleId()+"");
        }
        List<UserRole> userRoles = selectList(userRoleWrapper);
        PageInfo<UserRole> pageInfo = new PageInfo<>(userRoles);
        return new PageVo<>(pageNum, pageSize, pageInfo.getTotal(), userRoles);
    }

    @Override
    public ResponseEntity<String> assigningRoles(UserRoleExt userRoleExt) {
        List<String> roleIdArray=userRoleExt.getRoleIds();
        Wrapper<UserRole> userRoleWrapper=new EntityWrapper<>(new UserRole());
        userRoleWrapper.andNew().eq("user_id",userRoleExt.getUserId());
        delete(userRoleWrapper);
        for(String roleId:roleIdArray){
            if(!StringUtils.isEmpty(roleId)){
                UserRole userRole=new UserRole();
                userRole.setId(UuidUtil.next());
                userRole.setUserId(userRoleExt.getUserId());
                userRole.setRoleId(roleId);
                insert(userRole);
            }
        }
        return new ResponseEntity<>("success");
    }


}
