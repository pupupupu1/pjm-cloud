package com.pjm.circleoffriendservice.mapper;

import com.pjm.circleoffriendservice.entity.CircleOfFriendsComment;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.pjm.circleoffriendservice.entityExt.CircleOfFriendsCommentExt;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author pjm
 * @since 2021-01-25
 */
@Mapper
public interface CircleOfFriendsCommentMapper extends BaseMapper<CircleOfFriendsComment> {
    List<CircleOfFriendsCommentExt> getListByCircleId(String id);
}
