package com.pjm.nettyservice.socket.resolver4pjm;

import com.pjm.common.entity.PjmCloudUserLbsUser;
import com.pjm.common.util.UserUtil;
import com.pjm.common.util.common.StringUtil;
import com.pjm.nettyservice.socket.MessageTypeEnum4Pjm;
import com.pjm.nettyservice.socket.PjmMsgEntity;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SystemMessageResolver implements Resolver4Pjm {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserUtil userUtil;

    @Override
    public boolean support(PjmMsgEntity message) {
        return message.getAction().equals(MessageTypeEnum4Pjm.SYSTEM.getType());
    }

    @Override
    public void resolve(ChannelHandlerContext context, PjmMsgEntity message) {
        log.info("获取到系统内部消息：{}", message);
        String token = message.getHeader().get("token");
        if (StringUtil.isBlank(token)) {
            log.error("垃圾消息，干扰消息");
            return;
        }
        if ("quartz_loc_update".equals(message.getHeader().get("type"))) {
            log.info("这是一个用户经纬度坐标同步消息");
            String longitude = message.getHeader().get("longitude");
            String latitude = message.getHeader().get("latitude");
            String address = message.getHeader().get("address");
            String userAccount = userUtil.getAccount(token);

            Query query = new Query(Criteria.where("name").is(userAccount));
            List<Double> doubles = new ArrayList<>();
            doubles.add(Double.parseDouble(longitude));
            doubles.add(Double.parseDouble(latitude));
            Update update = new Update();
            update.set("loc", doubles);
            update.set("address", address);
            mongoTemplate.upsert(query, update, PjmCloudUserLbsUser.class);
            log.info("更新{}的实时坐标完成,他的位置是{}", userAccount, address);
        }
    }
}
