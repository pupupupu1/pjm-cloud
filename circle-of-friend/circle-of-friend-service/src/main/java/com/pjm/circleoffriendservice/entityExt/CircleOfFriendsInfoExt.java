package com.pjm.circleoffriendservice.entityExt;

import com.baomidou.mybatisplus.activerecord.Model;
import com.pjm.circleoffriendservice.entity.CircleOfFriendsComment;
import com.pjm.circleoffriendservice.entity.CircleOfFriendsInfo;
import com.pjm.userapi.entity.UserApi;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author pjm
 * @since 2021-01-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CircleOfFriendsInfoExt extends CircleOfFriendsInfo {
    private List<String> userAccounts;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private UserApi userApi;
    private List<CircleOfFriendsCommentExt> circleOfFriendsComments;
}