package com.pjm.rabbitmqservice.controller;


import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.rabbitmqservice.entity.MqLocalMessage;
import com.pjm.rabbitmqservice.entity.ext.MqLocalMessageExt;
import com.pjm.rabbitmqservice.service.IMqLocalMessageService;
import com.pjm.userapi.entity.UserApi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author pjm
 * @since 2021-02-10
 */
@Api(tags = {"消息总线控制器"})
@RestController
@RequestMapping("/mqLocalMessage")
public class MqLocalMessageController {
    @Autowired
    private IMqLocalMessageService mqLocalMessageService;

    @PostMapping("getUser")
    public UserApi getUser() {
        return new UserApi();
    }

    @ApiOperation("消息历史记录")
    @PostMapping("list")
    public ResponseEntity<PageVo<List<MqLocalMessage>>> list(@RequestBody MqLocalMessageExt mqLocalMessageExt){
        return ResponseEntity.success(mqLocalMessageService.listPageVo(mqLocalMessageExt,mqLocalMessageExt.getPageNum(),mqLocalMessageExt.getPageSize()));
    }
}

