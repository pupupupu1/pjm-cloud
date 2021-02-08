package com.pjm.userservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.common.exception.CustomException;
import com.pjm.common.redis.Cache;
import com.pjm.common.util.JedisUtil;
import com.pjm.common.util.UserUtil;
import com.pjm.common.util.common.UuidUtil;
import com.pjm.userapi.entity.ext.UserApiExt;
import com.pjm.userservice.entity.User;
import com.pjm.userservice.entityExt.UserExt;
import com.pjm.userservice.entity.UserFriendShip;
import com.pjm.userservice.entityExt.UserFriendShipExt;
import com.pjm.userservice.mapper.UserFriendShipMapper;
import com.pjm.userservice.service.IUserFriendShipService;
import com.pjm.userservice.service.IUserService;
import com.pjm.userservice.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author pjm
 * @since 2020-11-08
 */
@Service
@Transactional
@Slf4j
public class UserFriendShipServiceImpl extends ServiceImpl<UserFriendShipMapper, UserFriendShip> implements IUserFriendShipService {


    @Autowired
    private JedisUtil jedisUtil;
    @Autowired
    private IUserService userService;
    @Autowired
    private UserFriendShipMapper userFriendShipMapper;

    @Autowired
    private CommonUtil commonUtil;

    @Override
    public ResponseEntity<List<UserFriendShipExt>> applyList() {
        List<UserFriendShip> userFriendShips=userFriendShipMapper.applyList(commonUtil.getUserInfo().getId());
        List<UserFriendShipExt> res=userFriendShips.stream().map(item->{
            UserFriendShipExt temp=JSON.parseObject(JSON.toJSONString(item),UserFriendShipExt.class);
            //获取user信息，需要缓存(使用json字符串)
            String userJson = jedisUtil.getJson("cloud:cache:info:" + item.getUserId());
            if (Objects.isNull(userJson)) {
                //查询数据库(可能缓存击穿)
                UserExt friend = userService.detailsUser(new User().setId(item.getUserId()));
                userJson = JSON.toJSONString(friend);
                jedisUtil.setJson("cloud:cache:info:" + item.getUserId(), userJson, 3600);
            }
            User user = JSON.parseObject(userJson, User.class);
            temp.setUser(user);
            return temp;
        }).collect(Collectors.toList());
        return ResponseEntity.success(res);
    }

    @Override
    public ResponseEntity<String> request2AddFriends(UserFriendShip userFriendShip) {
        //单方面添加好友
        UserExt userExt = commonUtil.getUserInfo();
        userFriendShip.setUserId(userExt.getId());
        userFriendShip.setId(UuidUtil.next());
        userFriendShip.setRelatedStatus(0L);
        userFriendShip.setCreateTime(String.valueOf(System.currentTimeMillis()));
        //校验frienid的存在性
        userFriendShipMapper.insert(userFriendShip);
        return ResponseEntity.success("success");
    }

    @Override
    public ResponseEntity<String> agree2AddFriends(UserFriendShip userFriendShip) {
        //单方面添加好友
        UserExt userExt = commonUtil.getUserInfo();
        userFriendShip.setUserId(userExt.getId());
        userFriendShip.setId(UuidUtil.next());
        userFriendShip.setRelatedStatus(0L);
        //校验frienid的存在性
        userFriendShipMapper.insert(userFriendShip);
        return ResponseEntity.success("success");
    }

    @Override
    public ResponseEntity<String> updateFriendStatus(UserFriendShip userFriendShip) {
        //验证statu存在性
        //验证userid合法性
        //单方面修改好友状态
        Long status = userFriendShip.getRelatedStatus();
        UserFriendShip temp = new UserFriendShip();
        temp.setId(userFriendShip.getId());
        temp.setRelatedStatus(status);
        userFriendShipMapper.updateById(temp);
        return ResponseEntity.success("success");
    }

    @Override
    public ResponseEntity<String> deleteFriend(UserFriendShip userFriendShip) {
        UserExt userExt = commonUtil.getUserInfo();
        //双向删除，两条记录都删掉
        userFriendShipMapper.delete(new EntityWrapper<>(new UserFriendShip())
                .eq("user_id", userExt.getId()).
                        and("friend_user_id", userFriendShip.getFriendUserId())
                .andNew().or()
                .eq("user_id", userFriendShip.getFriendUserId()).
                        and("friend_user_id", userExt.getId()));
        return ResponseEntity.success("success");
    }

    /**
     * 需要redis以及mq二级缓存
     *
     * @param userFriendShipExt
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ResponseEntity<PageVo<List<UserFriendShipExt>>> friendList(UserFriendShipExt userFriendShipExt, Integer pageNum, Integer pageSize) {
        UserExt userExt = commonUtil.getUserInfo();
        //暂时不分页，不条件查询
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<UserFriendShip> userFriendShips = userFriendShipMapper.friendList(userExt.getId());
        PageInfo<UserFriendShip> pageInfo = new PageInfo<>(userFriendShips);
        List<UserFriendShipExt> userFriendShipExts = new LinkedList<>();
        userFriendShips.forEach(item -> {
            //获取user信息，需要缓存(使用json字符串)
            String userJson = jedisUtil.getJson("cloud:cache:info:" + item.getFriendUserId());
            if (Objects.isNull(userJson)) {
                //查询数据库(可能缓存击穿)
                UserExt friend = userService.detailsUser(new User().setId(item.getFriendUserId()));
                userJson = JSON.toJSONString(friend);
                jedisUtil.setJson("cloud:cache:info:" + item.getFriendUserId(), userJson, 3600000);
            }
            User user = JSON.parseObject(userJson, User.class);
            UserFriendShipExt temp = JSON.parseObject(JSON.toJSONString(item), UserFriendShipExt.class);
            temp.setUser(user);
            userFriendShipExts.add(temp);
        });
        return ResponseEntity.success(new PageVo<>(pageNum, pageSize, pageInfo.getTotal(), userFriendShipExts));
    }
}
