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
public class UserApi implements Serializable{

    private static final long serialVersionUID = 1L;

    private String id;

    private String userAccount;

    private String userPassword;

    private String userName;

    private String userTel;

    private String userPosition;

    private String userEmploy;

    private String userAddress;

    private String userHeader;

    private String enabled;
    private Long version;

}