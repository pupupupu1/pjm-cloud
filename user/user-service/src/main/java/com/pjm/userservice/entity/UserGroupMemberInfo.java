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
public class UserGroupMemberInfo extends Model<UserGroupMemberInfo> {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "群id")
    private String userGroupId;
    @ApiModelProperty(value = "群成员id")
    private String userGroupMemberId;
    @ApiModelProperty(value = "群成员身份（群主0，管理员1，普通群员2）")
    private String userGroupMemberPosition;
    @ApiModelProperty(value = "加群时间")
    private Long userGroupJoinTime;
    @ApiModelProperty(value = "加群状态（已加群1，申请中0）")
    private Long userGroupJoinStatus;
    @ApiModelProperty(value = "申请入群原因")
    private String userGroupJoinApplicationReason;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}