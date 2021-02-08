package com.pjm.nacosservice.controller;


import com.pjm.common.entity.ResponseEntity;
import com.pjm.nacosservice.entity.WhiteListFilter;
import com.pjm.nacosservice.entity.ext.WhiteListFilterExt;
import com.pjm.nacosservice.service.IWhiteListFilterService;
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
 * @since 2020-09-23
 */
@Api(tags = "拦截白名单控制器")
@RestController
@RequestMapping("/whiteListFilter")
public class WhiteListFilterController {
    @Autowired
    private IWhiteListFilterService whiteListFilterService;

    @ApiOperation("添加")
    @PostMapping("add")
    public ResponseEntity<String> add(@RequestBody List<WhiteListFilter> whiteListFilterList) {
        return whiteListFilterService.add(whiteListFilterList);
    }

    @ApiOperation("修改")
    @PostMapping("update")
    public ResponseEntity<String> update(@RequestBody WhiteListFilter whiteListFilter) {
        return whiteListFilterService.update(whiteListFilter);
    }

    @ApiOperation("删除")
    @PostMapping("delete")
    public ResponseEntity<String> delete(@RequestBody WhiteListFilterExt whiteListFilterExt) {
        return whiteListFilterService.delete(whiteListFilterExt);
    }
}

