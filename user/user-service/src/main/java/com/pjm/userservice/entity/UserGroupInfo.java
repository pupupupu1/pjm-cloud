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
public class UserGroupInfo extends Model<UserGroupInfo> {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "群聊名称")
    private String userGroupName;
    @ApiModelProperty(value = "群号码")
    private Long userGroupNumber;
    @ApiModelProperty(value = "群创建人id（群主id）")
    private String userGroupCreaterId;
    @ApiModelProperty(value = "群人数")
    private Integer userGroupNumOfPeople;
    @ApiModelProperty(value = "群通知")
    private String userGroupAnnouncement;
    @ApiModelProperty(value = "群头像地址")
    private String userGroupAvatarPath;
    @ApiModelProperty(value = "创建时间")
    private Long userGroupCreateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}