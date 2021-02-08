package com.pjm.userapi.entity.ext;

import com.pjm.userapi.entity.UserGroupInfoApi;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author pjm
 * @since 2020-11-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserGroupInfoApiExt extends UserGroupInfoApi {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private List<String> ids;
}