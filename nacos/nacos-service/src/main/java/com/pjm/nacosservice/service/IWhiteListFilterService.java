package com.pjm.nacosservice.service;

import com.baomidou.mybatisplus.service.IService;
import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.nacosservice.entity.WhiteListFilter;
import com.pjm.nacosservice.entity.ext.WhiteListFilterExt;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author pjm
 * @since 2020-09-23
 */
public interface IWhiteListFilterService extends IService<WhiteListFilter> {
    ResponseEntity<PageVo<List<WhiteListFilter>>> getList(WhiteListFilterExt whiteListFilterExt,Integer pageNum,Integer pageSize);
    Set<WhiteListFilter> getApplicationNameSet();
    Set<WhiteListFilter> getInterfaceNameSetByApplicationName(WhiteListFilter whiteListFilter);

    ResponseEntity<String> update(@Valid WhiteListFilter whiteListFilter);

    ResponseEntity<String> add( List<WhiteListFilter> whiteListFilterList);

    ResponseEntity<String> delete(WhiteListFilterExt whiteListFilterExt);

    PageVo<List<WhiteListFilterExt>> getTreeList(WhiteListFilterExt whiteListFilterExt,Integer pageNum,Integer pageSize);
}
