package com.pjm.rabbitmqservice.service;

import com.baomidou.mybatisplus.service.IService;
import com.pjm.common.entity.PageVo;
import com.pjm.rabbitmqservice.entity.MqLocalMessage;
import com.pjm.rabbitmqservice.entity.ext.MqLocalMessageExt;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author pjm
 * @since 2021-02-10
 */
public interface IMqLocalMessageService extends IService<MqLocalMessage> {
    List<MqLocalMessage> findNotSendMsg();
    List<MqLocalMessage> findCusmuerErrorMsg();
    PageVo<List<MqLocalMessage>> listPageVo(MqLocalMessageExt ext,Integer pageNum,Integer pageSize);
}
