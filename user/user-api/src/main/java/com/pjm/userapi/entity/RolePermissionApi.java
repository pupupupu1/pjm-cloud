package com.pjm.userapi.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author pjm
 * @since 2020-05-14
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RolePermissionApi implements Serializable{

    private static final long serialVersionUID = 1L;


    private String id;

    private String roleId;

    private String permissionId;
    private Long version;

}