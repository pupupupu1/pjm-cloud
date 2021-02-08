package com.pjm.userapi.service;

import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.userapi.entity.*;
import com.pjm.userapi.entity.ext.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Component(value="userClient")
@FeignClient(name = "pjm-service-user")
public interface UserClient {
    @GetMapping("/userApiClient/getUser")
    public UserApi getUser();

    @GetMapping("/userApiClient/initInfo")
    public ResponseEntity<UserApiExt> initInfo(@RequestParam("account") String account);

    @PostMapping("/userApiClient/findUserByAccountOrTel")
    public ResponseEntity<UserApi> findUserByAccountOrTel(@RequestBody UserApi user);

    @PostMapping("/permissionApiClient/selectList")
    public ResponseEntity<PageVo<List<PermissionApi>>> selectList(@RequestBody PermissionApiExt permissionExt);

    @PostMapping("/roleApiClient/selectList")
    public ResponseEntity<PageVo<List<RoleApi>>> selectList(@RequestBody RoleApiExt roleExt);

    @PostMapping("/rolePermissionApiClient/selectList")
    public ResponseEntity<PageVo<List<RolePermissionApi>>> selectList(@RequestBody RolePermissionApiExt rolePermissionExt);

    @PostMapping("/userRoleApiClient/selectList")
    public ResponseEntity<PageVo<List<UserRoleApi>>> selectList(@RequestBody UserRoleApiExt userRoleExt);
}
