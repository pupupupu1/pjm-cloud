package com.pjm.userapi.entity.ext;

import com.pjm.userapi.entity.UserRoleApi;
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
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class UserRoleApiExt extends UserRoleApi implements Serializable {
    private List<String> ids;
    private String roleIds;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private List<UserRoleApiExt> children;
}