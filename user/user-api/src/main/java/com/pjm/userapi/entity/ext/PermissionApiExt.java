package com.pjm.userapi.entity.ext;

import com.pjm.userapi.entity.PermissionApi;
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
public class PermissionApiExt extends PermissionApi implements Serializable {
    private List<String> ids;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private List<PermissionApiExt> children;

}