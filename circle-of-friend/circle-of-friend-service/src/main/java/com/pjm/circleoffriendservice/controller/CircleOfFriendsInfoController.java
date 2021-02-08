package com.pjm.circleoffriendservice.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.pjm.circleoffriendservice.entity.CircleOfFriendsComment;
import com.pjm.circleoffriendservice.entity.CircleOfFriendsInfo;
import com.pjm.circleoffriendservice.entityExt.CircleOfFriendsCommentExt;
import com.pjm.circleoffriendservice.entityExt.CircleOfFriendsInfoExt;
import com.pjm.circleoffriendservice.service.ICircleOfFriendsCommentService;
import com.pjm.circleoffriendservice.service.ICircleOfFriendsInfoService;
import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.common.util.JedisUtil;
import com.pjm.common.util.UserUtil;
import com.pjm.common.util.common.UuidUtil;
import com.pjm.userapi.entity.UserApi;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author pjm
 * @since 2021-01-25
 */
@Api(tags = {"朋友圈信息"})
@RestController
@RequestMapping("/circleOfFriendsInfo")
public class CircleOfFriendsInfoController {
    @Autowired
    private ICircleOfFriendsInfoService circleOfFriendsInfoService;
    @Autowired
    private ICircleOfFriendsCommentService circleOfFriendsCommentService;
    @Autowired
    private JedisUtil jedisUtil;
    @Autowired
    private UserUtil userUtil;
    @Autowired
    private HttpServletRequest request;

    @ApiOperation("添加朋友圈")
    @PostMapping("insert")
    public ResponseEntity<String> insert(@RequestBody CircleOfFriendsInfo circleOfFriendsInfo) {
        circleOfFriendsInfo.setCreateTime(String.valueOf(System.currentTimeMillis()));
        circleOfFriendsInfo.setUserAccount(userUtil.getAccount(request));
        circleOfFriendsInfo.setId(UuidUtil.next());
        circleOfFriendsInfoService.insert(circleOfFriendsInfo);
        return ResponseEntity.success("success");
    }

    @ApiOperation("删除朋友圈")
    @GetMapping("delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id) {
        String account = userUtil.getAccount(request);
        circleOfFriendsInfoService.delete(new EntityWrapper<>(new CircleOfFriendsInfo().setId(id).setUserAccount(account)));
        return ResponseEntity.success("success");
    }

    @ApiOperation("修改朋友圈")
    @PostMapping("update")
    public ResponseEntity<String> update(@RequestBody CircleOfFriendsInfo circleOfFriendsInfo) {
        String account = userUtil.getAccount(request);
        CircleOfFriendsInfo temp = new CircleOfFriendsInfo();
        temp.setId(circleOfFriendsInfo.getId());
        temp.setCreateTime(String.valueOf(System.currentTimeMillis()));
        temp.setTextContent(circleOfFriendsInfo.getTextContent());
        temp.setPicPath(circleOfFriendsInfo.getPicPath());
        circleOfFriendsInfoService.update(temp, new EntityWrapper(new CircleOfFriendsInfo().setId(temp.getId()).setUserAccount(account)));
//        circleOfFriendsInfoService.updateById(temp);
        return ResponseEntity.success("success");
    }

    @ApiOperation("查看朋友圈")
    @GetMapping("detail/{id}")
    public ResponseEntity<CircleOfFriendsInfoExt> detail(@PathVariable("id") String id) {
        CircleOfFriendsInfo temp = circleOfFriendsInfoService.selectById(id);
        CircleOfFriendsInfoExt res = JSON.parseObject(JSON.toJSONString(temp), CircleOfFriendsInfoExt.class);
//        String userJson = jedisUtil.getJson("cloud:cache:info:" + res.getUserAccount());
//        UserApi user = JSON.parseObject(userJson, UserApi.class);
//        res.setUserApi(user);
        List<CircleOfFriendsCommentExt> circleOfFriendsComments = circleOfFriendsCommentService.getListByCircleId(id);
        res.setCircleOfFriendsComments(circleOfFriendsComments);
        return ResponseEntity.success(res);
    }

    @ApiOperation("朋友圈列表")
    @PostMapping("getList")
    public ResponseEntity<PageVo<List<CircleOfFriendsInfo>>> getList(@RequestBody CircleOfFriendsInfoExt circleOfFriendsInfoExt) {
//        circleOfFriendsInfoExt.setUserAccount(userUtil.getAccount(request));
        return ResponseEntity.success(circleOfFriendsInfoService.getList(circleOfFriendsInfoExt, circleOfFriendsInfoExt.getPageNum(), circleOfFriendsInfoExt.getPageSize()));
    }
}

