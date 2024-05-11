package com.shang.backendquestionservice.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shang.backendcommon.constant.CommonConstant;
import com.shang.backendcommon.utils.SqlUtils;
import com.shang.backendmodel.model.dto.question.UserCommentDto;
import com.shang.backendmodel.model.dto.question.UserCommentGetDto;
import com.shang.backendmodel.model.entity.Question;
import com.shang.backendmodel.model.entity.User;
import com.shang.backendmodel.model.entity.UserComments;
import com.shang.backendmodel.model.vo.CommentVo;
import com.shang.backendmodel.model.vo.QuestionVO;
import com.shang.backendmodel.model.vo.UserVO;
import com.shang.backendquestionservice.mapper.UserCommentsMapper;
import com.shang.backendquestionservice.service.UserCommentsService;
import com.shang.backendserviceclient.service.UserFeignClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author 86189
* @description 针对表【user_comments(用户评论)】的数据库操作Service实现
* @createDate 2024-04-20 16:37:06
*/
@Service
public class UserCommentsServiceImpl extends ServiceImpl<UserCommentsMapper, UserComments>
    implements UserCommentsService {
    @Resource
    UserFeignClient userFeignClient;
    @Resource
    UserCommentsMapper usersCommentMapper;
    /**
     *查询条件
     */
    @Override
    public QueryWrapper<UserComments> getQueryWrapper(UserCommentGetDto userCommentDto) {
        QueryWrapper<UserComments> queryWrapper = new QueryWrapper<>();
        if (userCommentDto == null) {
            return queryWrapper;
        }
        Long questionid = userCommentDto.getQuestionid();
        String sortOrder = userCommentDto.getSortOrder();
        String sortField = userCommentDto.getSortField();
        // 拼接查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionid), "questionid", questionid);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_DESC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<CommentVo> getQuestionVOPage(Page<UserComments> page, HttpServletRequest request) {
        List<UserComments> List1 = page.getRecords();//拿到
        Page<CommentVo> commentVOPage = new Page<>(page.getCurrent(),page.getSize(), page.getTotal());
        if (CollectionUtils.isEmpty(List1)) {
            return commentVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = List1.stream().map(UserComments::getUserid).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userFeignClient.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        final int[] m = {0};
        int n=List1.size();


        // 填充信息
        List<CommentVo> commentVO = List1.stream().map(comment -> {
            Long userId = comment.getUserid();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            CommentVo userVO = new CommentVo();
            userVO.setId(comment.getId());
            userVO.setUserVO(userFeignClient.getUserVO(user));
            userVO.setUpdatetime(comment.getUpdatetime());
            userVO.setCollectscount(comment.getCollectscount());
            userVO.setCommentcontent(comment.getCommentcontent());
            userVO.setLikescount(comment.getLikescount());
            userVO.setQuestionid(comment.getQuestionid());
            return userVO;
        }).collect(Collectors.toList());
        commentVOPage.setRecords(commentVO);
        return commentVOPage;
    }

    @Override
    public boolean insert(UserComments userComments) {
        int insert = usersCommentMapper.insert(userComments);
        if (insert==1){
            return true;
        }
        return false;
    }
}




