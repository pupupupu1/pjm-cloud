package com.pjm.circleoffriendservice.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.pjm.circleoffriendservice.entity.CircleOfFriendsComment;
import com.pjm.circleoffriendservice.entityExt.CircleOfFriendsCommentExt;
import com.pjm.circleoffriendservice.service.ICircleOfFriendsCommentService;
import com.pjm.circleoffriendservice.service.ICircleOfFriendsInfoService;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.common.service.AsyncService;
import com.pjm.common.util.UserUtil;
import com.pjm.common.util.common.UuidUtil;
import com.pjm.nettyapi.service.NettyClientApi;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pjm
 * @since 2021-01-25
 */
@Api(tags = {"朋友圈评论"})
@RestController
@RequestMapping("/circleOfFriendsComment")
public class CircleOfFriendsCommentController {

    @Autowired
    private ICircleOfFriendsCommentService circleOfFriendsCommentService;
    @Autowired
    private UserUtil userUtil;
    @Autowired
    private HttpServletRequest request;
    @Resource
    private NettyClientApi nettyClientApi;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private ICircleOfFriendsInfoService circleOfFriendsInfoService;

    @ApiOperation("添加朋友圈评论")
    @PostMapping("insert")
    public ResponseEntity<String> insert(@RequestBody CircleOfFriendsComment circleOfFriendsComment) throws InterruptedException {
        circleOfFriendsComment.setCreateTime(String.valueOf(System.currentTimeMillis()));
        circleOfFriendsComment.setUserAccount(userUtil.getAccount(request));
        circleOfFriendsComment.setId(UuidUtil.next());
        circleOfFriendsCommentService.insert(circleOfFriendsComment);
        asyncService.asyncInvoke(() -> {
            Map<String, String> info = new HashMap<>();
            info.put("sourceUserAccount", circleOfFriendsComment.getUserAccount());
            info.put("optionUserAccount", circleOfFriendsInfoService.selectById(circleOfFriendsComment.getCircleId()).getUserAccount());
            nettyClientApi.callUser4CommentResponse(info);
        });
        return ResponseEntity.success(circleOfFriendsComment.getId());
    }

    @ApiOperation("删除朋友圈评论")
    @GetMapping("delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id) {
        String account = userUtil.getAccount(request);
        circleOfFriendsCommentService.delete(new EntityWrapper<>(new CircleOfFriendsComment().setId(id).setUserAccount(account)));
        return ResponseEntity.success("success");
    }

    @ApiOperation("修改朋友圈评论")
    @PostMapping("update")
    public ResponseEntity<String> update(@RequestBody CircleOfFriendsComment circleOfFriendsComment) {
        String account = userUtil.getAccount(request);
        CircleOfFriendsComment temp = new CircleOfFriendsComment();
        temp.setCreateTime(String.valueOf(System.currentTimeMillis()));
        temp.setContent(circleOfFriendsComment.getContent());
        circleOfFriendsCommentService.update(temp, new EntityWrapper<>(new CircleOfFriendsComment().setId(circleOfFriendsComment.getId()).setUserAccount(account)));
        return ResponseEntity.success("success");
    }

    @ApiOperation("查看朋友圈评论")
    @GetMapping("getListByCircleId/{id}")
    public ResponseEntity<List<CircleOfFriendsCommentExt>> getListByCircleId(@PathVariable("id") String id) {
        return ResponseEntity.success(circleOfFriendsCommentService.getListByCircleId(id));
    }

}

