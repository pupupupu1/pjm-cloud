package com.pjm.rabbitmqservice.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.pjm.rabbitmqservice.entity.MqLocalMessage;
import com.pjm.rabbitmqservice.mapper.MqLocalMessageMapper;
import com.pjm.rabbitmqservice.service.IMqLocalMessageService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author pjm
 * @since 2021-02-10
 */
@Service
public class MqLocalMessageServiceImpl extends ServiceImpl<MqLocalMessageMapper, MqLocalMessage> implements IMqLocalMessageService {

}
