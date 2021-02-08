package com.pjm.userapi.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author pjm
 * @since 2020-11-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserGroupInfoApi {

    private static final long serialVersionUID = 1L;
    private String id;

    private String userGroupName;

    private Long userGroupNumber;

    private String userGroupCreaterId;

    private Integer userGroupNumOfPeople;

    private String userGroupAnnouncement;

    private String userGroupAvatarPath;

    private Long userGroupCreateTime;

}