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
 * @since 2020-05-14
 */
@ApiModel(value = "")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class User extends Model<User> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "用户账号")
    private String userAccount;
    @ApiModelProperty(value = "用户密码")
    private String userPassword;
    @ApiModelProperty(value = "用户名")
    private String userName;
    @ApiModelProperty(value = "用户电话")
    private String userTel;
    @ApiModelProperty(value = "用户职位")
    private String userPosition;
    @ApiModelProperty(value = "用户单位")
    private String userEmploy;
    @ApiModelProperty(value = "用户地址")
    private String userAddress;
    @ApiModelProperty(value = "用户头像")
    private String userHeader;
    @ApiModelProperty(value = "是否可用")
    private String enabled;
    private Long version;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}