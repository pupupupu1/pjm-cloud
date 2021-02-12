package com.pjm.nacosservice.api;

import com.pjm.common.aop.cache.EnableCache;
import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.nacosservice.entity.WhiteListFilter;
import com.pjm.nacosservice.entity.ext.WhiteListFilterExt;
import com.pjm.nacosservice.service.IWhiteListFilterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("NacosApiClient")
public class NacosApiController {
    @Autowired
    private IWhiteListFilterService whiteListFilterService;

    @EnableCache(key = "WhiteListFilter", expirTime = 1000 * 60 * 60 * 24 * 15)
    @PostMapping("getApplicationNameSet")
//    @EnableCache(key = "ApplicationNameSet")
    public Set<WhiteListFilter> getApplicationNameSet() {
        log.info("getApplicationNameSet");
        return whiteListFilterService.getApplicationNameSet();
    }

    @EnableCache(key = "WhiteListFilter+$P0:filterCode", expirTime = 1000 * 60 * 60 * 24 * 15)
    @PostMapping("getInterfaceNameSetByApplicationName")
//    @EnableCache(key = "InterfaceNameSetByApplicationName")
    public Set<WhiteListFilter> getInterfaceNameSetByApplicationName(@RequestBody WhiteListFilter whiteListFilter) {
        return whiteListFilterService.getInterfaceNameSetByApplicationName(whiteListFilter);
    }

    @PostMapping("getList")
//    @EnableCache(key = "WhiteListFilterList")
    public ResponseEntity<PageVo<List<WhiteListFilter>>> getList(@RequestBody WhiteListFilterExt whiteListFilterExt) {
        return whiteListFilterService.getList(whiteListFilterExt, whiteListFilterExt.getPageNum(), whiteListFilterExt.getPageSize());
    }

}
