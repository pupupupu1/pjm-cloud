package com.pjm.userservice.api;


import com.pjm.userservice.entity.UserGroupInfo;
import com.pjm.userservice.entityExt.UserGroupMemberInfoExt;
import com.pjm.userservice.service.IUserGroupInfoService;
import com.pjm.userservice.service.IUserGroupMemberInfoService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Api(tags = {"用户聊天组的ApiClient"})
@RestController
@RequestMapping("/userGroupApiClient")
public class UserGroupApiController {
    @Autowired
    private IUserGroupMemberInfoService userGroupMemberInfoService;

    @GetMapping("getUserListByGroupId")
    public List<UserGroupMemberInfoExt> getUserListByGroupId(@RequestParam("id") String id){
        return userGroupMemberInfoService.getUserListByGroupId(id).getData();
    }
}
