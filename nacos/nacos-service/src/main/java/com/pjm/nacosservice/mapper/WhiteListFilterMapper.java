package com.pjm.nacosservice.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.pjm.nacosservice.entity.WhiteListFilter;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author pjm
 * @since 2020-09-23
 */
@Component
@Mapper
public interface WhiteListFilterMapper extends BaseMapper<WhiteListFilter> {

}
