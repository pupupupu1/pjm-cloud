package com.pjm.userservice.service;

import com.baomidou.mybatisplus.service.IService;
import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.userservice.entity.UserFriendShip;
import com.pjm.userservice.entityExt.UserFriendShipExt;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author pjm
 * @since 2020-11-08
 */
public interface IUserFriendShipService extends IService<UserFriendShip> {

    ResponseEntity<List<UserFriendShipExt>> applyList();
    ResponseEntity<String> request2AddFriends(UserFriendShip userFriendShip) throws InterruptedException;
    //如上接口是相同的接口
    ResponseEntity<String> agree2AddFriends(UserFriendShip userFriendShip);

    ResponseEntity<String> updateFriendStatus(UserFriendShip userFriendShip);

    ResponseEntity<String> deleteFriend(UserFriendShip userFriendShip);

    ResponseEntity<PageVo<List<UserFriendShipExt>>> friendList(UserFriendShipExt userFriendShipExt,Integer pageNum,Integer pageSize);
}
