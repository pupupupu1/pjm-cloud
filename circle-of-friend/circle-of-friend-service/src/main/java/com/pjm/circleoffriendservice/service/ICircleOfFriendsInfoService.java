package com.pjm.circleoffriendservice.service;

import com.pjm.circleoffriendservice.entity.CircleOfFriendsInfo;
import com.baomidou.mybatisplus.service.IService;
import com.pjm.circleoffriendservice.entityExt.CircleOfFriendsInfoExt;
import com.pjm.common.entity.PageVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author pjm
 * @since 2021-01-25
 */
public interface ICircleOfFriendsInfoService extends IService<CircleOfFriendsInfo> {
    public PageVo<List<CircleOfFriendsInfo>> getList(CircleOfFriendsInfoExt circleOfFriendsInfoExt,Integer pageNum,Integer pageSize);
}
