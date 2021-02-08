package com.pjm.userservice.entityExt;

import com.pjm.userservice.entity.Role;
import com.pjm.userservice.entity.User;
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
public class UserExt extends User {
    private List<String> ids;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private List<PermissionExt> permissionList;
    private List<Role> roleList;
}