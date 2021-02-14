package com.pjm.userservice.service;

import com.baomidou.mybatisplus.service.IService;
import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.userservice.entity.User;
import com.pjm.userservice.entityExt.UserExt;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author pjm
 * @since 2020-05-14
 */
public interface IUserService extends IService<User> {
    User findUserByAccountOrTel(User user);

    ResponseEntity<User> loginUser(User user);

    ResponseEntity<User> insertUser(User user);

    PageVo<List<User>> listWithPage(UserExt userExt, Integer pageNum, Integer pageSize);

    List<User> searchList(String searchkey);

    ResponseEntity<UserExt> initUser();

    ResponseEntity<UserExt> initUser(String account);

    UserExt detailsUser(User user);

    boolean logout(User user);

    User updatePassword(User user);

    User ResetPassword(User user);

    User updateMySelf(User user);
}
