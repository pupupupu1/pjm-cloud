package com.pjm.userservice.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.common.exception.CustomException;
import com.pjm.common.util.common.StringUtil;
import com.pjm.common.util.common.UuidUtil;
import com.pjm.userservice.entity.UserGroupInfo;
import com.pjm.userservice.entity.UserGroupMemberInfo;
import com.pjm.userservice.entityExt.UserExt;
import com.pjm.userservice.entityExt.UserGroupInfoExt;
import com.pjm.userservice.mapper.UserGroupInfoMapper;
import com.pjm.userservice.service.IUserGroupInfoService;
import com.pjm.userservice.service.IUserGroupMemberInfoService;
import com.pjm.userservice.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
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
 * @since 2020-11-08
 */
@Slf4j
@Service
@Transactional
public class UserGroupInfoServiceImpl extends ServiceImpl<UserGroupInfoMapper, UserGroupInfo> implements IUserGroupInfoService {

    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private IUserGroupMemberInfoService userGroupMemberInfoService;
    @Autowired
    private UserGroupInfoMapper userGroupInfoMapper;

    @Override
    public ResponseEntity<String> insertGroup(UserGroupInfo userGroupInfo) {
        UserExt userExt = commonUtil.getUserInfo();
        String groupId = UuidUtil.next();
        userGroupInfo.setId(groupId);
        userGroupInfo.setUserGroupCreaterId(userExt.getId());
        userGroupInfo.setUserGroupCreateTime(System.currentTimeMillis());
        userGroupInfo.setUserGroupNumOfPeople(1);
        if (insert(userGroupInfo)) {
            //添加自己进去,mq执行
            userGroupMemberInfoService.insert(new UserGroupMemberInfo()
                    .setId(UuidUtil.next())
                    .setUserGroupId(groupId).setUserGroupJoinStatus(1L).setUserGroupJoinTime(System.currentTimeMillis())
                    .setUserGroupMemberId(userExt.getId()).setUserGroupMemberPosition("0"));
            return ResponseEntity.success("success");
        }
        return ResponseEntity.failed("创建失败");
    }

    @Override
    public ResponseEntity<String> updateGroup(UserGroupInfo userGroupInfo) {
        UserExt userExt = commonUtil.getUserInfo();
        //判断是否是管理员或者群主
        if (false) {
            throw new CustomException("权限不足");
        }
        userGroupInfo.setUserGroupCreaterId(userExt.getId());
        if (updateById(userGroupInfo)) {
            return ResponseEntity.success("success");
        }
        return ResponseEntity.failed("修改失败");
    }

    @Override
    public ResponseEntity<String> deleteGroup(String id) {
        UserExt userExt = commonUtil.getUserInfo();
        //判断是否是管理员或者群主
        if (false) {
            throw new CustomException("权限不足");
        }
        deleteById(id);
        //随后需要删除成员表的信息，mp
        return ResponseEntity.success("success");
    }

    @Override
    public ResponseEntity<PageVo<List<UserGroupInfo>>> listGroup(UserGroupInfoExt userGroupInfoExt, Integer pageNum, Integer pageSize) {
        UserExt userExt = commonUtil.getUserInfo();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<UserGroupInfo> userGroupInfos = userGroupInfoMapper.userGroupList(userExt.getId());
        PageInfo<UserGroupInfo> pageInfo = new PageInfo<>(userGroupInfos);
        return ResponseEntity.success(new PageVo<>(pageNum, pageSize, pageInfo.getTotal(), pageInfo.getList()));
    }

    @Override
    public List<UserGroupInfo> findGroupList(String searchKey) {
        if (StringUtils.isEmpty(searchKey)) {
            return new ArrayList<>();
        }
        Wrapper<UserGroupInfo> wrapper = new EntityWrapper<>();
        wrapper.like(!StringUtils.isEmpty(searchKey), "user_group_number", searchKey).or().like(!StringUtils.isEmpty(searchKey), "user_group_name", searchKey);
        return selectList(wrapper);
    }
}
