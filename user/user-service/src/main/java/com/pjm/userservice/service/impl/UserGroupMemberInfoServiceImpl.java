package com.pjm.userservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.common.exception.CustomException;
import com.pjm.common.exception.LoginException;
import com.pjm.common.exception.PjmException;
import com.pjm.common.util.JedisUtil;
import com.pjm.common.util.UserUtil;
import com.pjm.common.util.common.UuidUtil;
import com.pjm.userservice.entity.User;
import com.pjm.userservice.entity.UserGroupMemberInfo;
import com.pjm.userservice.entityExt.UserExt;
import com.pjm.userservice.entityExt.UserGroupInfoExt;
import com.pjm.userservice.entityExt.UserGroupMemberInfoExt;
import com.pjm.userservice.mapper.UserGroupMemberInfoMapper;
import com.pjm.userservice.service.IUserGroupInfoService;
import com.pjm.userservice.service.IUserGroupMemberInfoService;
import com.pjm.userservice.service.IUserService;
import com.pjm.userservice.util.CommonUtil;
import com.sun.scenario.effect.impl.sw.java.JSWEffectPeer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

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
public class UserGroupMemberInfoServiceImpl extends ServiceImpl<UserGroupMemberInfoMapper, UserGroupMemberInfo> implements IUserGroupMemberInfoService {

    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private JedisUtil jedisUtil;
    @Autowired
    private IUserService userService;
    @Autowired
    private IUserGroupInfoService userGroupInfoService;

    @Override
    public ResponseEntity<String> applicationJoinGroup(UserGroupMemberInfo userGroupMemberInfo) {
        UserExt userExt = commonUtil.getUserInfo();
        //单方面添加一条status为0的记录
        userGroupMemberInfo.setId(UuidUtil.next());
        userGroupMemberInfo.setUserGroupJoinStatus(0L);
        userGroupMemberInfo.setUserGroupMemberId(userExt.getId());
        insert(userGroupMemberInfo);
        return ResponseEntity.success("success");
    }

    @Override
    public ResponseEntity<String> agree2JoinGroup(UserGroupMemberInfo userGroupMemberInfo) {
        UserExt userExt = commonUtil.getUserInfo();
        //判断职位是否是管理员或者群主
        UserGroupMemberInfo queryRes = selectOne(new EntityWrapper<>(new UserGroupMemberInfo())
                .eq("user_group_id", userGroupMemberInfo.getUserGroupId())
                .eq("user_group_member_id", userExt.getId()));
        if ("2".equals(queryRes.getUserGroupMemberPosition())) {
            throw new PjmException(500,"群权限不足");
        }
        String id = userGroupMemberInfo.getId();
        UserGroupMemberInfo temp = new UserGroupMemberInfo()
                .setId(id)
                .setUserGroupMemberId(userGroupMemberInfo.getUserGroupMemberId())
                .setUserGroupJoinTime(System.currentTimeMillis())
                .setUserGroupMemberPosition("2")
                .setUserGroupJoinStatus(1L);
        updateById(temp);
        //添加一个成员数目
        return ResponseEntity.success("success");
    }

    @Override
    public ResponseEntity<String> removeFromGroup(UserGroupMemberInfoExt userGroupMemberInfoExt) {
        UserExt userExt = commonUtil.getUserInfo();
        //判断是否是管理员或者群主
        if (false) {
            throw new CustomException("权限不足");
        }
        List<String> ids = userGroupMemberInfoExt.getIds();
        //mybatis plus的列表bug，需要至少有一个
        ids.add("0");
        String groupId = userGroupMemberInfoExt.getUserGroupId();
        Wrapper<UserGroupMemberInfo> wrapper = new EntityWrapper<>(new UserGroupMemberInfo());
        wrapper.eq("user_group_id", groupId).and().in("user_group_member_id", ids);
        delete(wrapper);
        //减少n个成员
        return ResponseEntity.success("success");
    }

    @Override
    public ResponseEntity<List<UserGroupMemberInfoExt>> getUserListByGroupId(String groupId) {
        //此处全部可以缓存
        List<UserGroupMemberInfoExt> userGroupMemberInfoExts = null;

//        userGroupMemberInfoExts = JSON.parseArray(jedisUtil.getJson("cloud:cache:group:member:" + groupId), UserGroupMemberInfoExt.class);

        List<UserGroupMemberInfo> userGroupMemberInfos = selectList(new EntityWrapper<>(new UserGroupMemberInfo().setUserGroupId(groupId)));
        userGroupMemberInfoExts = userGroupMemberInfos.stream().map(item -> {
            UserGroupMemberInfoExt temp = JSON.parseObject(JSON.toJSONString(item), UserGroupMemberInfoExt.class);

//            String userJson = jedisUtil.getJson("cloud:cache:info:" + temp.getUserGroupMemberId());
//            if (Objects.isNull(userJson)) {
//                //查询数据库(可能缓存击穿)
//                UserExt temp2 = userService.detailsUser(new User().setId(temp.getUserGroupMemberId()));
//                userJson = JSON.toJSONString(temp2);
//                jedisUtil.setJson("cloud:cache:info:" + temp.getUserGroupMemberId(), userJson,3600000);
//            }
//            User user = JSON.parseObject(userJson, User.class);
            User user = userService.detailsUser(new User().setId(temp.getUserGroupMemberId()));
            temp.setUser(user);
            return temp;
        }).collect(Collectors.toList());
//        jedisUtil.setJson("cloud:cache:group:member:" + groupId, JSON.toJSONString(userGroupMemberInfoExts),3600000);
        return ResponseEntity.success(userGroupMemberInfoExts);
    }


    @Override
    public List<UserGroupMemberInfoExt> getTheReviewListOfGroupMembershipApplicationRelatedToMe() {
        UserExt userExt = commonUtil.getUserInfo();
        Wrapper<UserGroupMemberInfo> wrapper = new EntityWrapper<>();
        wrapper.eq("user_group_member_id", userExt.getId()).in("user_group_member_position", new Integer[]{0, 1});
        List<UserGroupMemberInfo> adminGroupList = selectList(wrapper);
        List<UserGroupMemberInfoExt> allUserGroupMemberInfoExts = new ArrayList<>();
        adminGroupList.forEach(adminGroup -> {
            Wrapper<UserGroupMemberInfo> wrapper2 = new EntityWrapper<>();
            wrapper2.eq("user_group_id", adminGroup.getUserGroupId()).eq("user_group_join_status", 0);
            List<UserGroupMemberInfo> userGroupMemberInfos = selectList(wrapper2);
            List<UserGroupMemberInfoExt> userGroupMemberInfoExts = JSON.parseArray(JSON.toJSONString(userGroupMemberInfos), UserGroupMemberInfoExt.class);
            userGroupMemberInfoExts.forEach(item -> {
                item.setUser(userService.detailsUser(new User().setId(item.getUserGroupMemberId())));
                item.setUserGroupInfo(userGroupInfoService.selectById(item.getUserGroupId()));
            });
            allUserGroupMemberInfoExts.addAll(userGroupMemberInfoExts);
        });
        return allUserGroupMemberInfoExts;
    }
}
