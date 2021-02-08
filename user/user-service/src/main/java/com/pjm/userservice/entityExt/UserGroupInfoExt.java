package com.pjm.userservice.entityExt;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.pjm.userservice.entity.UserGroupInfo;
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
public class UserGroupInfoExt extends UserGroupInfo {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private List<String> ids;
}