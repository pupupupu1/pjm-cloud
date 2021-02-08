package com.pjm.circleoffriendservice.entity;

import com.baomidou.mybatisplus.activerecord.Model;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author pjm
 * @since 2021-01-25
 */
@ApiModel(value = "")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CircleOfFriendsComment extends Model<CircleOfFriendsComment> {

    private static final long serialVersionUID = 1L;

    private String id;
    @ApiModelProperty(value = "回复id")
    private String replyId;
    @ApiModelProperty(value = "回复的人的账号")
    private String replyAccount;
    @ApiModelProperty(value = "朋友圈id")
    private String circleId;
    @ApiModelProperty(value = "内容")
    private String content;
    @ApiModelProperty(value = "版本")
    private Integer version;
    @ApiModelProperty(value = "创建时间")
    private String createTime;
    @ApiModelProperty(value = "评价账号")
    private String userAccount;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}