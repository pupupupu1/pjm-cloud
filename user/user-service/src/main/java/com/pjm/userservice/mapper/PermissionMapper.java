package com.pjm.userservice.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.pjm.userservice.entity.Permission;
import com.pjm.userservice.entityExt.PermissionExt;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author pjm
 * @since 2020-05-14
 */
@Component
@Repository
public interface PermissionMapper extends BaseMapper<Permission> {
    List<PermissionExt> selectWithPageWithChildren(Permission permission);
}
