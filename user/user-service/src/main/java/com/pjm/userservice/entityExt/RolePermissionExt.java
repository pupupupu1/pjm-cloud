package com.pjm.userservice.entityExt;

import com.pjm.userservice.entity.RolePermission;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author pjm
 * @since 2020-05-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RolePermissionExt extends RolePermission {
    private List<String> ids;
    private List<String> permissionIds;
    private List<String> roleIds;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private List<RolePermissionExt> children;
}