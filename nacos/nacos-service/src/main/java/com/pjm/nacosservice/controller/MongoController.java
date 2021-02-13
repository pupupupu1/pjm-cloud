package com.pjm.nacosservice.controller;

import com.pjm.common.entity.PjmCloudUserLbsUser;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.common.util.UserUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("mongo")
@RefreshScope
public class MongoController {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserUtil userUtil;
    @Autowired
    private HttpServletRequest request;

    @Value("${com.pjm.user.near.distance}")
    private double distance;

    @ApiOperation("根据坐标获取附近的人")
    @PostMapping("nearPropleByLbs")
    public ResponseEntity<List<GeoResult<PjmCloudUserLbsUser>>> nearPropleByLbs(@RequestBody PjmCloudUserLbsUser pjmCloudUserLbsUser) {
        String userAccount = userUtil.getAccount(request);
        NearQuery near = NearQuery
                .near(new GeoJsonPoint(pjmCloudUserLbsUser.getLoc().get(0), pjmCloudUserLbsUser.getLoc().get(1)))
                .spherical(true)
                .distanceMultiplier(6378137)
                .maxDistance(distance, Metrics.KILOMETERS);
        List<GeoResult<PjmCloudUserLbsUser>> geoResults = mongoTemplate.geoNear(near, PjmCloudUserLbsUser.class).getContent();
        geoResults=new ArrayList<>(geoResults);
        geoResults.removeIf(item ->item.getContent().getName().equals(userAccount));
        log.info("geoResults:{}",geoResults);
        return ResponseEntity.success(geoResults);
    }
}
