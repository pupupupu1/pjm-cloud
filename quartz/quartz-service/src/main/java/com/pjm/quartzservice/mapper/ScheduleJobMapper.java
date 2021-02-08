package com.pjm.quartzservice.mapper;

import com.pjm.quartzservice.entity.ScheduleJob;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author pjm
 * @since 2020-05-25
 */
@Mapper
@Component
public interface ScheduleJobMapper extends BaseMapper<ScheduleJob> {

}
