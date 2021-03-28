package com.pjm.nacosservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pjm.common.aop.cache.RefreshCache;
import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.common.exception.PjmException;
import com.pjm.common.util.common.UuidUtil;
import com.pjm.nacosservice.entity.WhiteListFilter;
import com.pjm.nacosservice.entity.ext.WhiteListFilterExt;
import com.pjm.nacosservice.mapper.WhiteListFilterMapper;
import com.pjm.nacosservice.service.IWhiteListFilterService;
import com.pjm.rabbitmqapi.entity.MessageMq;
import com.pjm.rabbitmqapi.service.MqApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author pjm
 * @since 2020-09-23
 */
@Transactional
@Service
public class WhiteListFilterServiceImpl extends ServiceImpl<WhiteListFilterMapper, WhiteListFilter> implements IWhiteListFilterService {

    @Autowired
    private WhiteListFilterMapper whiteListFilterMapper;
    @Resource
    private MqApiClient mqApiClient;

    @Override
    public ResponseEntity<PageVo<List<WhiteListFilter>>> getList(WhiteListFilterExt whiteListFilterExt, Integer pageNum, Integer pageSize) {
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        Wrapper<WhiteListFilter> wrapper = new EntityWrapper<>(new WhiteListFilter());
        if (!StringUtils.isEmpty(whiteListFilterExt.getId())) {
            wrapper.andNew().eq("id", whiteListFilterExt.getId());
        }
        if (!CollectionUtils.isEmpty(whiteListFilterExt.getIds())) {
            whiteListFilterExt.getIds().add("999999");
            wrapper.andNew().in("id", whiteListFilterExt.getIds());
        }
        if (!StringUtils.isEmpty(whiteListFilterExt.getFilterName())) {
            wrapper.andNew().like("filter_name", whiteListFilterExt.getFilterName());
        }
        if (!StringUtils.isEmpty(whiteListFilterExt.getFilterCode())) {
            wrapper.andNew().like("filter_code", whiteListFilterExt.getFilterCode());
        }
        if (!StringUtils.isEmpty(whiteListFilterExt.getFilterType())) {
            wrapper.andNew().eq("filter_type", whiteListFilterExt.getFilterType() + "");
        }
        if (!StringUtils.isEmpty(whiteListFilterExt.getFilterParentId())) {
            wrapper.andNew().eq("filter_parent_id", whiteListFilterExt.getFilterParentId());
        }
        List<WhiteListFilter> whiteListFilters = selectList(wrapper);
        PageInfo<WhiteListFilter> pageInfo = new PageInfo<>(whiteListFilters);
        return ResponseEntity.success(new PageVo<>(pageNum, pageSize, pageInfo.getTotal(), whiteListFilters));
    }

    @Override
    public Set<WhiteListFilter> getApplicationNameSet() {
        Wrapper<WhiteListFilter> wrapper = new EntityWrapper<>(new WhiteListFilter());
        wrapper.eq("filter_type", "1");
        return new HashSet<>(whiteListFilterMapper.selectList(wrapper));
    }

    @Override
    public Set<WhiteListFilter> getInterfaceNameSetByApplicationName(WhiteListFilter whiteListFilter) {
        whiteListFilter = whiteListFilterMapper.selectOne(whiteListFilter.setFilterType(1d));
        Wrapper<WhiteListFilter> wrapper = new EntityWrapper<>(new WhiteListFilter());
        wrapper.eq("filter_parent_id", whiteListFilter.getId());
        return new HashSet<>(whiteListFilterMapper.selectList(wrapper));
    }

    @Override
    @RefreshCache(key = "WhiteListFilter")
    public ResponseEntity<String> update(WhiteListFilter whiteListFilter) {
        whiteListFilterMapper.updateAllColumnById(whiteListFilter);
//        mqApiClient.add2Qunue(new MessageMq<>()
//                .setId("gateway-update."+UuidUtil.next())
//                .setExchangeName("pjm.topic2")
//                .setQueueName("gateway-update")
//                .setMessageBody(whiteListFilter));
        return ResponseEntity.success("success");
    }

    @Override
    @RefreshCache(key = "WhiteListFilter")
    public ResponseEntity<String> add(List<WhiteListFilter> whiteListFilterList) {
        whiteListFilterList.forEach(item -> {
            item.setId(UuidUtil.next());
        });
        insertBatch(whiteListFilterList);
//        mqApiClient.add2Qunue(new MessageMq<>()
//                .setId("gateway-add."+UuidUtil.next())
//                .setExchangeName("pjm.topic2")
//                .setQueueName("gateway-add")
//                .setMessageBody(whiteListFilterList));
        return ResponseEntity.success("success");
    }

    @Override
    @RefreshCache(key = "WhiteListFilter")
    public ResponseEntity<String> delete(WhiteListFilterExt whiteListFilterExt) {
        List<WhiteListFilterExt> whiteListFilters = getTreeList(whiteListFilterExt, 0, 0).getList();
        whiteListFilters.forEach(item -> {
            List<WhiteListFilter> tempChilren = item.getChildren();
            if (!CollectionUtils.isEmpty(tempChilren)) {
//                throw new PjmException(500,"部分白名单存在子层级，无法删除，请先删除子层级");
                //将子名单删除干净
                List<String> childIds = tempChilren.stream().map(WhiteListFilter::getId).collect(Collectors.toList());
                whiteListFilterMapper.deleteBatchIds(childIds);
            }
        });
        whiteListFilterMapper.deleteBatchIds(whiteListFilterExt.getIds());
        return ResponseEntity.success("success");
    }

    @Override
    public PageVo<List<WhiteListFilterExt>> getTreeList(WhiteListFilterExt whiteListFilterExt, Integer pageNum, Integer pageSize) {
        whiteListFilterExt.setFilterType(1d);
        PageVo<List<WhiteListFilter>> pageVo = getList(whiteListFilterExt, pageNum, pageSize).getData();
        List<WhiteListFilterExt> parentListExt = JSON.parseArray(JSON.toJSONString(pageVo.getList()), WhiteListFilterExt.class);
        parentListExt.forEach(item -> {
            WhiteListFilterExt queryDto = new WhiteListFilterExt();
            queryDto.setFilterParentId(item.getId());
            List<WhiteListFilter> childList = getList(queryDto, 0, 0).getData().getList();
            item.setChildren(childList);
        });
        return new PageVo<>(pageNum, pageSize, pageVo.getTotal(), parentListExt);
    }
}
