package com.pjm.nacosservice.api;

import com.pjm.common.aop.cache.EnableCache;
import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.nacosservice.entity.WhiteListFilter;
import com.pjm.nacosservice.entity.ext.WhiteListFilterExt;
import com.pjm.nacosservice.service.IWhiteListFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("NacosApiClient")
public class NacosApiController {
    @Autowired
    private IWhiteListFilterService whiteListFilterService;

    @PostMapping("getApplicationNameSet")
    @EnableCache(key = "ApplicationNameSet")
    public Set<WhiteListFilter> getApplicationNameSet() {
        return whiteListFilterService.getApplicationNameSet();
    }

    @PostMapping("getInterfaceNameSetByApplicationName")
    @EnableCache(key = "InterfaceNameSetByApplicationName")
    public Set<WhiteListFilter> getInterfaceNameSetByApplicationName(@RequestBody WhiteListFilter whiteListFilter) {
        return whiteListFilterService.getInterfaceNameSetByApplicationName(whiteListFilter);
    }

    @PostMapping("getList")
    @EnableCache(key = "WhiteListFilterList")
    public ResponseEntity<PageVo<List<WhiteListFilter>>> getList(@RequestBody WhiteListFilterExt whiteListFilterExt) {
        return whiteListFilterService.getList(whiteListFilterExt, whiteListFilterExt.getPageNum(), whiteListFilterExt.getPageSize());
    }

}
