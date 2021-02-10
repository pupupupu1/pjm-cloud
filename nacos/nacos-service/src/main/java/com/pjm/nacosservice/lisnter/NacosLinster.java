package com.pjm.nacosservice.lisnter;

import com.alibaba.fastjson.JSON;
import com.pjm.common.util.JedisUtil;
import com.pjm.common.util.common.SerializableUtil;
import com.pjm.nacosservice.entity.WhiteListFilter;
import com.pjm.nacosservice.entity.ext.WhiteListFilterExt;
import com.pjm.nacosservice.service.IWhiteListFilterService;
import com.pjm.userapi.service.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class NacosLinster {
    @Resource
    private UserClient userClient;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private JedisUtil jedisUtil;

    @Autowired
    private IWhiteListFilterService whiteListFilterService;

    @RabbitListener(queues = "gateway-update")
    public void gateWayUpdateLinster(Message msg) {
        WhiteListFilter msgEntity = (WhiteListFilter)JSON.parseArray(JSON.toJSONString(SerializableUtil.unserializable(msg.getBody())), WhiteListFilter.class).get(0);
        log.info("gateway.update被监听到了！{}", msg);
        if (msgEntity.getFilterType() == 1) {
            Set<WhiteListFilter> whiteListFilterSet = (Set<WhiteListFilter>) jedisUtil.getObject("cloud:cache:whiteList");
            whiteListFilterSet.removeIf(item->item.getId().equals(msgEntity.getId()));
            whiteListFilterSet.add(msgEntity);
            jedisUtil.setObject("cloud:cache:whiteList",whiteListFilterSet);
        } else {
            WhiteListFilterExt query = new WhiteListFilterExt();
            query.setPageNum(1).setPageSize(1)
                    .setId(whiteListFilterService
                            .getList(new WhiteListFilterExt()
                                    .setIds(Collections.singletonList(msgEntity.getId())).setPageNum(1).setPageSize(1),0,0)
                            .getData().getList().get(0).getFilterParentId());
            List<WhiteListFilter> whiteListFilterList = whiteListFilterService.getList(query,0,0).getData().getList();
            Set<WhiteListFilter> whiteListFilterSet = (Set<WhiteListFilter>) jedisUtil.getObject("cloud:cache:whiteList:" + whiteListFilterList.get(0).getFilterCode());
            whiteListFilterSet.removeIf(item->item.getId().equals(msgEntity.getId()));
            whiteListFilterSet.add(msgEntity);
            jedisUtil.setObject("cloud:cache:whiteList:" + whiteListFilterList.get(0).getFilterCode(),whiteListFilterSet);
        }
        log.info("gateway.update被监听到了！{}", msg);
    }

    @RabbitListener(queues = "gateway-add")
    public void gateWayAddLinster(Message msg) {
        WhiteListFilter msgEntity = (WhiteListFilter)JSON.parseArray(JSON.toJSONString(SerializableUtil.unserializable(msg.getBody())), WhiteListFilter.class).get(0);
        log.info("gateway.add被监听到了！{}", msg);
        if (msgEntity.getFilterType() == 1) {
            Set<WhiteListFilter> whiteListFilterSet = (Set<WhiteListFilter>) jedisUtil.getObject("cloud:cache:whiteList");
            whiteListFilterSet.removeIf(item->item.getId().equals(msgEntity.getId()));
            jedisUtil.setObject("cloud:cache:whiteList",whiteListFilterSet);
        } else {
            WhiteListFilterExt query = new WhiteListFilterExt();
            query.setPageNum(1).setPageSize(1)
                    .setId(whiteListFilterService
                            .getList(new WhiteListFilterExt()
                                    .setIds(Collections.singletonList(msgEntity.getId())).setPageNum(1).setPageSize(1),0,0)
                            .getData().getList().get(0).getFilterParentId());
            List<WhiteListFilter> whiteListFilterList = whiteListFilterService.getList(query,0,0).getData().getList();
            Set<WhiteListFilter> whiteListFilterSet = (Set<WhiteListFilter>) jedisUtil.getObject("cloud:cache:whiteList:" + whiteListFilterList.get(0).getFilterCode());
            whiteListFilterSet.removeIf(item->item.getId().equals(msgEntity.getId()));
            jedisUtil.setObject("cloud:cache:whiteList:" + whiteListFilterList.get(0).getFilterCode(),whiteListFilterSet);
        }
        log.info("gateway.add被监听到了！{}", msg);
    }

    @RabbitListener(queues = "gateway-delete")
    public void gateWayDeleteLinster(Message msg) {
        WhiteListFilter msgEntity = (WhiteListFilter)JSON.parseArray(JSON.toJSONString(SerializableUtil.unserializable(msg.getBody())), WhiteListFilter.class).get(0);
        log.info("gateway.delete被监听到了！{}", msg);
        if (msgEntity.getFilterType() == 1) {
            Set<WhiteListFilter> whiteListFilterSet = (Set<WhiteListFilter>) jedisUtil.getObject("cloud:cache:whiteList");
            whiteListFilterSet.add(msgEntity);
            jedisUtil.setObject("cloud:cache:whiteList",whiteListFilterSet);
        } else {
            WhiteListFilterExt query = new WhiteListFilterExt();
            query.setPageNum(1).setPageSize(1)
                    .setId(whiteListFilterService
                            .getList(new WhiteListFilterExt()
                                    .setIds(Collections.singletonList(msgEntity.getId())).setPageNum(1).setPageSize(1),0,0)
                            .getData().getList().get(0).getFilterParentId());
            List<WhiteListFilter> whiteListFilterList = whiteListFilterService.getList(query,0,0).getData().getList();
            Set<WhiteListFilter> whiteListFilterSet = (Set<WhiteListFilter>) jedisUtil.getObject("cloud:cache:whiteList:" + whiteListFilterList.get(0).getFilterCode());
            whiteListFilterSet.add(msgEntity);
            jedisUtil.setObject("cloud:cache:whiteList:" + whiteListFilterList.get(0).getFilterCode(),whiteListFilterSet);
        }
        log.info("gateway.delete被监听到了！{}", msg);
    }
}
