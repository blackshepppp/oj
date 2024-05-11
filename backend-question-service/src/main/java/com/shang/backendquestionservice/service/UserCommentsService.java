package com.shang.backendquestionservice.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shang.backendmodel.model.dto.question.UserCommentGetDto;
import com.shang.backendmodel.model.entity.UserComments;
import com.shang.backendmodel.model.vo.CommentVo;

import javax.servlet.http.HttpServletRequest;

/**
* @author 86189
* @description 针对表【user_comments(用户评论)】的数据库操作Service
* @createDate 2024-04-20 16:37:06
*/
public interface UserCommentsService extends IService<UserComments> {


    QueryWrapper<UserComments> getQueryWrapper(UserCommentGetDto userCommentDto);

    Page<CommentVo> getQuestionVOPage(Page<UserComments> page, HttpServletRequest request);

    boolean insert(UserComments userComments);
}
