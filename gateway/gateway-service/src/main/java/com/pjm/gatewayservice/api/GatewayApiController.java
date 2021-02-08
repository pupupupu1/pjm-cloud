package com.pjm.gatewayservice.api;

import com.pjm.common.entity.ResponseEntity;
//import com.pjm.gatewayservice.shiro.util.UserUtil;
//import io.swagger.annotations.Api;
import com.pjm.common.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.ServerPortInfoApplicationContextInitializer;
import org.springframework.boot.web.reactive.context.ReactiveWebApplicationContext;
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Api(tags = {"服务路由微服务接口"})
@RestController
@RequestMapping("/gatewayApi")
public class GatewayApiController {

}
