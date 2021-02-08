package com.pjm.circleoffriendservice.entityExt;

import com.baomidou.mybatisplus.activerecord.Model;
import com.pjm.circleoffriendservice.entity.CircleOfFriendsComment;
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
@ApiModel(value = "")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CircleOfFriendsCommentExt extends CircleOfFriendsComment {
    private List<String> ids;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private List<CircleOfFriendsCommentExt> children;
    private Boolean deleteEnable;
}