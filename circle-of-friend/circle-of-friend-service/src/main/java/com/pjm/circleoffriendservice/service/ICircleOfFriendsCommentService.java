package com.pjm.circleoffriendservice.service;

import com.pjm.circleoffriendservice.entity.CircleOfFriendsComment;
import com.baomidou.mybatisplus.service.IService;
import com.pjm.circleoffriendservice.entityExt.CircleOfFriendsCommentExt;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author pjm
 * @since 2021-01-25
 */
public interface ICircleOfFriendsCommentService extends IService<CircleOfFriendsComment> {
    List<CircleOfFriendsCommentExt> getListByCircleId(String id);
}
