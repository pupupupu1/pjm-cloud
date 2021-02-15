package com.pjm.userservice.service;

import com.baomidou.mybatisplus.service.IService;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.userservice.entity.User;
import com.pjm.userservice.entity.UserGroupMemberInfo;
import com.pjm.userservice.entityExt.UserGroupMemberInfoExt;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author pjm
 * @since 2020-11-08
 */
public interface IUserGroupMemberInfoService extends IService<UserGroupMemberInfo> {
    ResponseEntity<String> applicationJoinGroup(UserGroupMemberInfo userGroupMemberInfo);

    ResponseEntity<String> agree2JoinGroup(UserGroupMemberInfo userGroupMemberInfo);

    ResponseEntity<String> removeFromGroup(UserGroupMemberInfoExt userGroupMemberInfoExt);

    ResponseEntity<List<UserGroupMemberInfoExt>> getUserListByGroupId(String groupId);

    List<UserGroupMemberInfoExt> getTheReviewListOfGroupMembershipApplicationRelatedToMe();
}
