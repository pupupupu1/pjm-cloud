package com.pjm.userservice.entityExt;

import com.pjm.userservice.entity.User;
import com.pjm.userservice.entity.UserFriendShip;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserFriendShipExt extends UserFriendShip {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private List<String> ids;
    private User user;
}
