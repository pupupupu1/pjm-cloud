package com.pjm.userservice.service;

import com.baomidou.mybatisplus.service.IService;
import com.pjm.common.entity.PageVo;
import com.pjm.userservice.entity.Role;
import com.pjm.userservice.entityExt.RoleExt;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author pjm
 * @since 2020-05-14
 */
public interface IRoleService extends IService<Role> {
    PageVo<List<Role>> listWithPage(RoleExt roleExt, Integer pageNum, Integer pageSize);

}
