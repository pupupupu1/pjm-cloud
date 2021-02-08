package com.pjm.userapi.entity.ext;

import com.pjm.userapi.entity.UserApi;
import com.pjm.userapi.entity.UserGroupMemberInfoApi;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author pjm
 * @since 2020-11-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserGroupMemberInfoApiExt extends UserGroupMemberInfoApi {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private List<String> ids;
    private UserApi user;
}