package com.pjm.nacosapi.service;

import com.pjm.common.aop.cache.EnableCache;
import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.nacosapi.entity.WhiteListFilter;
import com.pjm.nacosapi.entity.ext.WhiteListFilterExt;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;

@Component
@FeignClient("pjm-service-nacos")
public interface NacosApiClient {

//    @EnableCache(key = "WhiteListFilter",expirTime = 1000 * 60 * 60 * 24 * 15)
    @PostMapping("NacosApiClient/getApplicationNameSet")
    Set<WhiteListFilter> getApplicationNameSet();

//    @EnableCache(key = "WhiteListFilter+$P0:filterCode", expirTime = 1000 * 60 * 60 * 24 * 15)
    @PostMapping("NacosApiClient/getInterfaceNameSetByApplicationName")
    Set<WhiteListFilter> getInterfaceNameSetByApplicationName(@RequestBody WhiteListFilter whiteListFilter);

    @PostMapping("NacosApiClient/getList")
    ResponseEntity<PageVo<List<WhiteListFilter>>> getList(@RequestBody WhiteListFilterExt whiteListFilterExt);
}
