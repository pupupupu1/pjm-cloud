package com.pjm.userapi.entity.ext;

import com.pjm.userapi.entity.UserApi;
import com.pjm.userapi.entity.UserFriendShipApi;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserFriendShipApiExt extends UserFriendShipApi {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private List<String> ids;
    private UserApi user;
}
