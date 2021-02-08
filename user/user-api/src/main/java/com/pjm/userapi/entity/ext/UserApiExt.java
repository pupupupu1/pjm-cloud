package com.pjm.userapi.entity.ext;

import com.pjm.userapi.entity.RoleApi;
import com.pjm.userapi.entity.UserApi;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author pjm
 * @since 2020-05-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserApiExt extends UserApi implements Serializable {
    private List<String> ids;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private List<PermissionApiExt> permissionList;
    private List<RoleApi> roleApiList;
}