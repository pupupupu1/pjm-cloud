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
import java.security.PrivateKey;

/**
 * @author pjm
 * @since 2020-05-14
 */
@ApiModel(value = "")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Permission extends Model<Permission> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "权限code(泛指接口名)")
    private String permissionCode;
    @ApiModelProperty(value = "权限类型code（0数据权限1菜单权限）")
    private String permissionTypeCode;
    @ApiModelProperty("权限application名称")
    private String permissionApplicationCode;
    @ApiModelProperty(value = "权限名")
    private String permissionName;
    @ApiModelProperty(value = "父id")
    private String parentId;
    private Long version;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}