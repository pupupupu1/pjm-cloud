package com.pjm.rabbitmqservice.mapper;

import com.pjm.rabbitmqservice.entity.MqLocalMessage;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author pjm
 * @since 2021-02-10
 */
@Mapper
public interface MqLocalMessageMapper extends BaseMapper<MqLocalMessage> {

}
