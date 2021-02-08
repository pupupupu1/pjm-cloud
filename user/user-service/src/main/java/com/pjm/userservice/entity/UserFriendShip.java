package com.pjm.userservice.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author pjm
 * @since 2020-11-08
 */
@ApiModel(value = "")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserFriendShip extends Model<UserFriendShip> {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.UUID)
    private String id;
    private String userId;
    @ApiModelProperty(value = "好友id")
    private String friendUserId;
    @ApiModelProperty(value = "关系状态（单方面）")
    private Long relatedStatus;
    @ApiModelProperty(value = "创建时间")
    private String createTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}