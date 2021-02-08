package com.pjm.gatewayservice.filter.auth;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.pjm.common.exception.CustomException;
import com.pjm.common.redis.Cache;
import com.pjm.common.util.JedisUtil;
import com.pjm.common.util.UserUtil;
import com.pjm.userapi.entity.RoleApi;
import com.pjm.userapi.entity.ext.PermissionApiExt;
import com.pjm.userapi.entity.ext.UserApiExt;
import com.pjm.userapi.service.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

//@Component
@RefreshScope
@Slf4j
public class GatewayInterceptFilter extends BaseGateWayAbsFilter {
    @Autowired
    private Cache cache;
    @Autowired
    private JedisUtil jedisUtil;
    @Autowired
    private UserUtil userUtil;
    @Resource
    private UserClient userClient;
    @Value("${com.pjm.filter.enable.intercept}")
    private boolean filterEnable;

    public void foreachTree(PermissionApiExt permissionExt, Set<String> permissionSet, String reqApplicationName){

        if (permissionExt.getPermissionApplicationCode().equals(reqApplicationName)){
            permissionSet.add(permissionExt.getPermissionCode());
        }
        if (CollectionUtils.isEmpty(permissionExt.getChildren())){
            return;
        }else {
            permissionExt.getChildren().forEach(item->{
                foreachTree(item,permissionSet,reqApplicationName);
            });
        }
    }
    @Override
    public void doFilter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!filterEnable) {
            log.info("GatewayWhiteListFilter未开启");
            if (this.next == null) {
                return;
            } else {
                next.doFilter(exchange, chain);
            }
//            throw new CustomException("测试异常CustomException");
        }
        System.out.println("InterceptFilter");
        //从缓存获取用户信息
        UserApiExt subject=(UserApiExt) cache.get(userUtil.getToken(exchange));
        if (Objects.isNull(subject)){
            //如果用户信息没有，就从数据库加载
            subject=userClient.initInfo(userUtil.getAccount(exchange)).getData();
            if (Objects.isNull(subject)){
                throw new CustomException("无效用户");
            }
            //同步进redis
            cache.put(userUtil.getToken(exchange),subject);
        }
        //获取角色以及权限列表
        List<RoleApi> roleApiList =subject.getRoleApiList();
        List<PermissionApiExt> permissionExtList=subject.getPermissionList();
        Set<String> applicationSet=new HashSet<>(permissionExtList.stream()
                .map(PermissionApiExt::getPermissionApplicationCode)
                .collect(Collectors.toList()));
        if (CollectionUtils.isEmpty(applicationSet)){
            throw new CustomException("权限不足");
        }
        Set<String> interfaceSet=new HashSet<>();
        //遍历权限树
        permissionExtList.forEach(item->{
            foreachTree(item,interfaceSet,userUtil.getReqApplicationName(exchange));
        });
        if (CollectionUtils.isEmpty(interfaceSet)){
            throw new CustomException("权限不足");
        }
        //判断权限
        if (applicationSet.contains(userUtil.getReqApplicationName(exchange))) {
            if (interfaceSet.contains(userUtil.getInterFaceName(exchange))) {
                log.info("欢迎你!!!!!!!!!!!!");
                return;
            } else {
                //抛出异常，不满足权限
                log.error("interface权限不足");
                throw new CustomException("interface权限不足");
            }
        } else {
            //抛出异常，不满足权限
            log.error("application权限不足");

        }
        if (next == null) {
//            chain.filter(exchange);
            throw new CustomException("application权限不足");
        } else {
            next.doFilter(exchange, chain);
        }
    }

    public BaseGateWayAbsFilter next;
}
