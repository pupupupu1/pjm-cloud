package com.pjm.userservice.entityExt;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.pjm.userservice.entity.User;
import com.pjm.userservice.entity.UserGroupMemberInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author pjm
 * @since 2020-11-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserGroupMemberInfoExt extends UserGroupMemberInfo {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private List<String> ids;
    private User user;
}