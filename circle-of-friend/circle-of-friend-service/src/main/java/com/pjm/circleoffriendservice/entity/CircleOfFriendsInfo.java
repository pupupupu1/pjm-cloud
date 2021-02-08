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
public class CircleOfFriendsInfo extends Model<CircleOfFriendsInfo> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "朋友圈id")
    private String id;
    @ApiModelProperty(value = "文字内容")
    private String textContent;
    @ApiModelProperty(value = "图片路径，分号间隔")
    private String picPath;
    @ApiModelProperty(value = "发布人账号")
    private String userAccount;
    @ApiModelProperty(value = "发布时间")
    private String createTime;
    @ApiModelProperty(value = "转发id")
    private String forwardId;
    @ApiModelProperty(value = "点赞数")
    private Integer likesNum;
    @ApiModelProperty(value = "版本")
    private Integer version;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}