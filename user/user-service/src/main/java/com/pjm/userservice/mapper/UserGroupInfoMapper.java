package com.pjm.userservice.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.pjm.userservice.entity.UserGroupInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author pjm
 * @since 2020-11-08
 */
@Mapper
public interface UserGroupInfoMapper extends BaseMapper<UserGroupInfo> {
    List<UserGroupInfo> userGroupList(String userId);
}
