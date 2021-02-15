package com.pjm.userservice.service;

import com.baomidou.mybatisplus.service.IService;
import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.userservice.entity.UserGroupInfo;
import com.pjm.userservice.entityExt.UserGroupInfoExt;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author pjm
 * @since 2020-11-08
 */
public interface IUserGroupInfoService extends IService<UserGroupInfo> {
    ResponseEntity<String> insertGroup(UserGroupInfo userGroupInfo);
    ResponseEntity<String> updateGroup(UserGroupInfo userGroupInfo);
    ResponseEntity<String> deleteGroup(String id);
    ResponseEntity<PageVo<List<UserGroupInfo>>> listGroup(UserGroupInfoExt userGroupInfoExt, Integer pageNum, Integer pageSize);
    List<UserGroupInfo> findGroupList(String searchKey);
    UserGroupInfoExt details(String id);
}
