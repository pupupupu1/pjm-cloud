package com.pjm.userservice.service;

import com.baomidou.mybatisplus.service.IService;
import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.userservice.entity.User;
import com.pjm.userservice.entity.UserRole;
import com.pjm.userservice.entityExt.UserRoleExt;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author pjm
 * @since 2020-05-14
 */
public interface IUserRoleService extends IService<UserRole> {
    List<UserRole> getRoleByUser(User user);

    PageVo<List<UserRole>> listWithPage(UserRoleExt userRoleExt, Integer pageNum, Integer pageSize);

    ResponseEntity<String> assigningRoles(UserRoleExt userRoleExt);
}
