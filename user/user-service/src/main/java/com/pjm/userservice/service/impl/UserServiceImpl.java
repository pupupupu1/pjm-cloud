package com.pjm.userservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pjm.common.common.Constant;
import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.common.exception.CustomException;
import com.pjm.common.exception.LoginException;
import com.pjm.common.util.*;
import com.pjm.gatewayapi.service.GatewayClient;
import com.pjm.userservice.entity.*;
import com.pjm.userservice.entityExt.PermissionExt;
import com.pjm.userservice.entityExt.UserExt;
import com.pjm.userservice.mapper.UserMapper;
import com.pjm.userservice.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author pjm
 * @since 2020-05-14
 */
@Slf4j
@Service
@Transactional
@PropertySource("classpath:config.properties")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    /**
     * RefreshToken过期时间
     */
    @Value("${refreshTokenExpireTime}")
    private String refreshTokenExpireTime;
    @Autowired
    private HttpServletResponse response;
    @Autowired
    private HttpServletRequest request;
    @Resource
    private GatewayClient gatewayClient;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private IPermissionService permissionService;
    @Autowired
    private IRolePermissionService rolePermissionService;
    @Autowired
    private IUserRoleService userRoleService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JedisUtil jedisUtil;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserUtil userUtil;

    @Override
    public User findUserByAccountOrTel(User user) {
        return selectOne(new EntityWrapper<>(new User()).andNew().
                eq("user_account", user.getUserAccount()).or().eq("user_tel", user.getUserTel()));
    }

    @Override
    public ResponseEntity<User> loginUser(User user) {
        // 查询数据库中的帐号信息
        User userTemp = new User();
        userTemp.setUserAccount(user.getUserAccount());
        userTemp = findUserByAccountOrTel(user);
        if (userTemp == null) {
            throw new LoginException(500, "该帐号不存在(The account does not exist.)");
        }
        // 密码进行AES解密
        String key = AesCipherUtil.deCrypto(userTemp.getUserPassword());
        // 因为密码加密是以帐号+密码的形式进行加密的，所以解密后的对比是帐号+密码
        if (key.equals(userTemp.getUserAccount() + user.getUserPassword())) {
            // 清除可能存在的Shiro权限信息缓存
            if (jedisUtil.exists(Constant.PREFIX_SHIRO_CACHE + user.getUserAccount())) {
                jedisUtil.delKey(Constant.PREFIX_SHIRO_CACHE + user.getUserAccount());
            }
            // 设置RefreshToken，时间戳为当前时间戳，直接设置即可(不用先删后设，会覆盖已有的RefreshToken)
            String currentTimeMillis = String.valueOf(System.currentTimeMillis());
            //refresh的value设置为用户名
            jedisUtil.setObject(Constant.PREFIX_SHIRO_REFRESH_TOKEN + user.getUserAccount(), user.getUserAccount(), Integer.parseInt(refreshTokenExpireTime));
            // 从Header中Authorization返回AccessToken，时间戳为当前时间戳,登记的value设置为登录人用户名
            String token = jwtUtil.sign(user.getUserAccount(), String.valueOf(System.currentTimeMillis()));
            log.info("登录成功，{}", token);
            response.setHeader("Authorization", token);
            response.setHeader("Access-Control-Expose-Headers", "Authorization");
            userTemp.setUserPassword(null);
            return ResponseEntity.success(userTemp, token);
        } else {
            throw new LoginException(500, "帐号或密码错误(Account or Password Error.)");
        }
    }

    @Override
    public ResponseEntity<User> insertUser(User user) {
        // 判断当前帐号是否存在
        User userTemp = new User();
        userTemp.setUserAccount(user.getUserAccount());
        userTemp = findUserByAccountOrTel(userTemp);
        if (userTemp != null && !StringUtils.isEmpty(userTemp.getUserPassword())) {
            throw new LoginException(500, "该帐号已存在(Account exist.)");
        }
        // 密码以帐号+密码的形式进行AES加密
        if (user.getUserAccount().length() > Constant.PASSWORD_MAX_LEN) {
            throw new LoginException(500, "密码最多8位(Password up to 8 bits.)");
        }
        String key = AesCipherUtil.enCrypto(user.getUserAccount() + user.getUserPassword());
        user.setUserPassword(key);
        int result = userMapper.insert(user);
//        默认注册的人为超级管理员
//        UserRole userRole = new UserRole();
//        userRole.setRoleId(1);
//        userRole.setUserId(user.getId());
//        userRoleService.insert(userRole);
        return new ResponseEntity<>(user);
    }

    @Override
    public PageVo<List<User>> listWithPage(UserExt userExt, Integer pageNum, Integer pageSize) {
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        Wrapper<User> userWrapper = new EntityWrapper<>(new User());
        if (!StringUtils.isEmpty(userExt.getId())) {
            userWrapper.andNew().like("id", userExt.getId() + "");
        }
        if (!StringUtils.isEmpty(userExt.getUserAccount())) {
            userWrapper.andNew().like("user_account", userExt.getUserAccount());
        }
        if (!StringUtils.isEmpty(userExt.getUserName())) {
            userWrapper.andNew().like("user_name", userExt.getUserName());
        }
        if (!StringUtils.isEmpty(userExt.getUserAddress())) {
            userWrapper.andNew().like("user_address", userExt.getUserAddress());
        }
        if (!StringUtils.isEmpty(userExt.getUserHeader())) {
            userWrapper.andNew().like("user_header", userExt.getUserHeader());
        }
        if (!StringUtils.isEmpty(userExt.getUserEmploy())) {
            userWrapper.andNew().like("user_employ", userExt.getUserEmploy());
        }
        if (!StringUtils.isEmpty(userExt.getUserPosition())) {
            userWrapper.andNew().like("user_position", userExt.getUserPosition());
        }
        if (!StringUtils.isEmpty(userExt.getUserTel())) {
            userWrapper.andNew().like("user_tel", userExt.getUserTel());
        }
        List<User> userList = selectList(userWrapper);
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        return new PageVo<>(pageNum, pageSize, pageInfo.getTotal(), userList);
    }

    @Override
    public List<User> searchList(String searchkey) {
        if (StringUtils.isEmpty(searchkey)) {
            return new ArrayList<>();
        }
        Wrapper<User> userWrapper = new EntityWrapper<>(new User());
        userWrapper.like("user_account", searchkey).orNew().like("user_name", searchkey);
        return selectList(userWrapper);
    }

    @Override
    public ResponseEntity<UserExt> initUser() {
        User user = findUserByAccountOrTel(new User().setUserAccount(userUtil.getAccount(request)));
        UserExt userExt = JSON.parseObject(JSON.toJSONString(user), UserExt.class);
        userExt = getInfoByUser(userExt);
        userExt.setUserPassword(null);
        return new ResponseEntity<>(userExt);
    }

    @Override
    public ResponseEntity<UserExt> initUser(String account) {
        User user = findUserByAccountOrTel(new User().setUserAccount(account));
        UserExt userExt = JSON.parseObject(JSON.toJSONString(user), UserExt.class);
        userExt = getInfoByUser(userExt);
        userExt.setUserPassword(null);
        return ResponseEntity.success(userExt);
    }

    public UserExt getInfoByUser(UserExt userExt) {
        List<String> userRoleList = userRoleService.selectList(new EntityWrapper<>(new UserRole()).andNew().eq("user_id", userExt.getId())).stream().map(userRole -> {
            return userRole.getRoleId() + "";
        }).collect(Collectors.toList());
        userRoleList.add("99999");
        List<Role> roleList = roleService.selectList(new EntityWrapper<>(new Role()).andNew().in("id", userRoleList));
        List<String> roleids = roleList.stream().map(role -> {
            return role.getId() + "";
        }).collect(Collectors.toList());
        roleids.add("99999");
        List<String> rolepermissionids = rolePermissionService.selectList(new EntityWrapper<>(new RolePermission()).andNew().in("role_id", roleids)).stream().map(rolePermission -> {
            return rolePermission.getPermissionId() + "";
        }).collect(Collectors.toList());
        rolepermissionids.add("99999");
        List<Permission> permissionList = permissionService.selectList(new EntityWrapper<>(new Permission()).andNew().in("id", rolepermissionids));
        List<PermissionExt> permissionExtList = JSON.parseArray(JSON.toJSONString(permissionList), PermissionExt.class);
        List<PermissionExt> permissionExtListTree = permissionService.convertMenuTree(permissionExtList);
        userExt.setRoleList(roleList);
        userExt.setPermissionList(permissionExtListTree);
        return userExt;
    }

    @Override
    public UserExt detailsUser(User user) {
        if (StringUtils.isEmpty(user.getId())) {
            return initUser(user.getUserAccount()).getData();
        }
        user = selectById(user.getId());
        if (user != null) {
            UserExt userExt = JSON.parseObject(JSON.toJSONString(user), UserExt.class);
            userExt = getInfoByUser(userExt);
            userExt.setUserPassword(null);
            return userExt;
        } else {
            throw new CustomException("没有此用户");
        }
    }

    @Override
    public boolean logout(User user) {
        user = findUserByAccountOrTel(new User().setId(userUtil.getAccount(request)));
        if (jedisUtil.exists(Constant.PREFIX_SHIRO_CACHE + user.getUserAccount())) {
            jedisUtil.delKey(Constant.PREFIX_SHIRO_CACHE + user.getUserAccount());
        }
        if (jedisUtil.exists(Constant.PREFIX_SHIRO_REFRESH_TOKEN + user.getUserAccount())) {
            jedisUtil.delKey(Constant.PREFIX_SHIRO_REFRESH_TOKEN + user.getUserAccount());
        }
        return true;
    }

    @Override
    public User updatePassword(User user) {
        if (user.getUserPassword().length() > Constant.PASSWORD_MAX_LEN) {
            throw new LoginException(500, "密码最多8位(Password up to 8 bits.)");
        }
        String pwd = user.getUserPassword();
        user = findUserByAccountOrTel(new User().setId(userUtil.getAccount(request)));
        String dekey = AesCipherUtil.deCrypto(user.getUserPassword());
        if ((pwd + user.getUserAccount()).equals(dekey)) {
            throw new LoginException(500, "修改后密码不能和原密码重复");
        }
        String key = AesCipherUtil.enCrypto(user.getUserAccount() + pwd);
        user.setUserPassword(key);
        updateById(user);
        return user;
    }

    @Override
    public User ResetPassword(User user) {
        user = selectById(user.getId());
        String pwd = VerificationCode.getVC(6);
        String key = AesCipherUtil.enCrypto(user.getUserAccount() + pwd);
        user.setUserPassword(key);
        updateById(user);
        user.setUserPassword(pwd);
        return user;
    }

    @Override
    public User updateMySelf(User user) {
        String userAccount = userUtil.getAccount(request);
        UserExt quertDto = new UserExt();
        quertDto.setUserAccount(userAccount);
        User userTemp = listWithPage(quertDto, 0, 0).getList().get(0);
        user.setId(userTemp.getId());
        user.setEnabled(null);
        user.setUserPassword(null);
        user.setVersion(null);
        updateById(user);
        return user;
    }

}
