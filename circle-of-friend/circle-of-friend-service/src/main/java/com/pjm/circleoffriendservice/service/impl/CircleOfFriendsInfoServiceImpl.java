package com.pjm.circleoffriendservice.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pjm.circleoffriendservice.entity.CircleOfFriendsInfo;
import com.pjm.circleoffriendservice.entityExt.CircleOfFriendsInfoExt;
import com.pjm.circleoffriendservice.mapper.CircleOfFriendsInfoMapper;
import com.pjm.circleoffriendservice.service.ICircleOfFriendsInfoService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.pjm.common.entity.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author pjm
 * @since 2021-01-25
 */
@Service
public class CircleOfFriendsInfoServiceImpl extends ServiceImpl<CircleOfFriendsInfoMapper, CircleOfFriendsInfo> implements ICircleOfFriendsInfoService {
    @Autowired
    private CircleOfFriendsInfoMapper circleOfFriendsInfoMapper;

    @Override
    public PageVo<List<CircleOfFriendsInfo>> getList(CircleOfFriendsInfoExt circleOfFriendsInfoExt, Integer pageNum, Integer pageSize) {
        //userAccount必填，需要鉴别这个account是否是本人或者好友account，暂时不处理
        if (pageNum > 0 && pageSize > 0) {
            int total = selectCount(new EntityWrapper<>());
            if (total / pageSize < pageNum - 1) {
                return new PageVo<>(pageNum, pageSize, total, new ArrayList<>());
            }
            PageHelper.startPage(pageNum, pageSize);
        }

        Wrapper<CircleOfFriendsInfo> wrapper = new EntityWrapper<>(new CircleOfFriendsInfo());
        wrapper.like(!StringUtils.isEmpty(circleOfFriendsInfoExt.getTextContent()), "text_content", circleOfFriendsInfoExt.getTextContent());
        wrapper.eq(!StringUtils.isEmpty(circleOfFriendsInfoExt.getUserAccount()), "user_account", circleOfFriendsInfoExt.getUserAccount());
        wrapper.in(!CollectionUtils.isEmpty(circleOfFriendsInfoExt.getUserAccounts()), "user_account", circleOfFriendsInfoExt.getUserAccounts());
        wrapper.orderDesc(Collections.singletonList("create_time"));

        List<CircleOfFriendsInfo> circleOfFriendsInfos = circleOfFriendsInfoMapper.selectList(wrapper);
        PageInfo<CircleOfFriendsInfo> pageInfo = new PageInfo<>(circleOfFriendsInfos);

        return new PageVo<>(pageNum, pageSize, pageInfo.getTotal(), pageInfo.getList());
    }
}
