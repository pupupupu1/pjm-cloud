package com.pjm.userservice.entityExt;

import com.pjm.userservice.entity.Role;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author pjm
 * @since 2020-05-14
 */
@ApiModel(value = "")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RoleExt extends Role {

    private List<String> ids;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private List<RoleExt> children;
}