package com.pjm.rabbitmqservice.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.pjm.rabbitmqservice.entity.MqLocalMessage;
import com.pjm.rabbitmqservice.mapper.MqLocalMessageMapper;
import com.pjm.rabbitmqservice.service.IMqLocalMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author pjm
 * @since 2021-02-10
 */
@Service
public class MqLocalMessageServiceImpl extends ServiceImpl<MqLocalMessageMapper, MqLocalMessage> implements IMqLocalMessageService {

    @Override
    public List<MqLocalMessage> findNotSendMsg() {
        Wrapper<MqLocalMessage> wrapper = new EntityWrapper<>();
        wrapper.eq("msg_status", "0");
        return selectList(wrapper);
    }

    @Override
    public List<MqLocalMessage> findCusmuerErrorMsg() {
        Wrapper<MqLocalMessage> wrapper = new EntityWrapper<>();
        wrapper.eq("msg_status",  "2");
        return selectList(wrapper);

    }
}
