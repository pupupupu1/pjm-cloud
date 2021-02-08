package com.pjm.userservice.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.pjm.userservice.entity.Role;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author pjm
 * @since 2020-05-14
 */
@Component
@Repository
public interface RoleMapper extends BaseMapper<Role> {

}
