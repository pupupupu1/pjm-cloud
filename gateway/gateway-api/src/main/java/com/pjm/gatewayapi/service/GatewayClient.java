package com.pjm.gatewayapi.service;

import com.pjm.common.entity.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@FeignClient("gateway-service")
public interface GatewayClient {
//    @PostMapping("/gatewayApi/getUserAccount")
//    public ResponseEntity<String> getUserAccount(@RequestBody HttpServletRequest request);
//
//    @PostMapping("/getUserToken")
//    public ResponseEntity<String> getUserToken(@RequestBody HttpServletRequest request);
}
