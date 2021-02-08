package com.pjm.circleoffriendservice.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.pjm.circleoffriendservice.entity.CircleOfFriendsComment;
import com.pjm.circleoffriendservice.entityExt.CircleOfFriendsCommentExt;
import com.pjm.circleoffriendservice.mapper.CircleOfFriendsCommentMapper;
import com.pjm.circleoffriendservice.service.ICircleOfFriendsCommentService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.pjm.common.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author pjm
 * @since 2021-01-25
 */
@Slf4j
@Service
public class CircleOfFriendsCommentServiceImpl extends ServiceImpl<CircleOfFriendsCommentMapper, CircleOfFriendsComment> implements ICircleOfFriendsCommentService {

    @Autowired
    private CircleOfFriendsCommentMapper circleOfFriendsCommentMapper;
    @Autowired
    private UserUtil userUtil;
    @Autowired
    private HttpServletRequest request;
    private String account;

    @Override
    public List<CircleOfFriendsCommentExt> getListByCircleId(String id) {
        List<CircleOfFriendsCommentExt> treeData = circleOfFriendsCommentMapper.getListByCircleId(id);
        this.account = userUtil.getAccount(request);
        treeData.forEach(item -> {
            item.setDeleteEnable(item.getUserAccount().equals(account));
            List<CircleOfFriendsCommentExt> children = new ArrayList<>();
            tree22D(item, children);
            item.setChildren(children);
        });
        return treeData;
    }

    private void tree22D(CircleOfFriendsCommentExt temp, List<CircleOfFriendsCommentExt> res) {
        temp.setDeleteEnable(temp.getUserAccount().equals(account));
        if (!CollectionUtils.isEmpty(temp.getChildren())) {
            res.addAll(temp.getChildren());
            temp.getChildren().forEach(item -> {
                tree22D(item, res);
            });
        }
    }

    private List<CircleOfFriendsCommentExt> convertTree22DList(List<CircleOfFriendsCommentExt> tree) {
        try {
            tree.forEach(item -> {
                if (CollectionUtils.isEmpty(item.getChildren())) {
                    item.setChildren(new ArrayList<>());
                } else {
                    List<CircleOfFriendsCommentExt> itemChildren = item.getChildren();
                    if (!CollectionUtils.isEmpty(itemChildren)) {
                        itemChildren.forEach(item2 -> {
                            to2D(item2, itemChildren);
                        });
                    }
                }
            });
        } catch (Exception e) {
            log.error("异常", e);
        }
        return tree;
    }

    private void to2D(CircleOfFriendsCommentExt temp, List<CircleOfFriendsCommentExt> parentList) {
        if (!CollectionUtils.isEmpty(temp.getChildren())) {
            parentList.addAll(temp.getChildren());
            temp.getChildren().forEach(item -> {
                to2D(item, parentList);
            });
        }
    }
}
