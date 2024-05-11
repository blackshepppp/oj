package com.shang.backendquestionservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shang.backendmodel.model.entity.CommentLike;
import com.shang.backendmodel.model.entity.UserComments;
import com.shang.backendquestionservice.service.CommentLikeService;
import com.shang.backendquestionservice.service.UserCommentsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ScheduledCommmet {

    @Resource
    CommentLikeService commentLikeService;
    @Resource
    UserCommentsService userCommentsService;
    static Map<Long, Integer> commentMap = new HashMap<>();//这个是存放到user——comment表中的评论记录着喜欢的数量
    static Map<Long, Long> commentLikeMap = new HashMap<>();//用户喜欢的评论
    @Scheduled(fixedRate = 120000) // 每120秒执行一次
    public void doSomething() {
        //todo 定时存放到数据库
        commentMap.forEach((commentId,likeCount)->{
            LambdaQueryWrapper<UserComments> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(UserComments::getId,commentId);
            if (userCommentsService.getOne(lambdaQueryWrapper)!=null){
                UserComments userComments = userCommentsService.getOne(lambdaQueryWrapper);
                userComments.setLikescount(likeCount);
                userCommentsService.updateById(userComments);
            }else {
                UserComments userComments = new UserComments();
                userComments.setId(commentId);
                userComments.setLikescount(likeCount);
                userCommentsService.save(userComments);
            }
        });
        log.info("定时存放到数据库{}" ,commentMap.toString());
        commentMap.clear();
        commentLikeMap.forEach((commentId,userId)->{
            LambdaQueryWrapper<CommentLike> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(CommentLike::getUserId,userId);
            lambdaQueryWrapper.eq(CommentLike::getCommentId,commentId);
            if (commentLikeService.getOne(lambdaQueryWrapper)!=null){
                CommentLike commentLike = commentLikeService.getOne(lambdaQueryWrapper);
                commentLike.setIsLike(1);
                commentLikeService.updateById(commentLike);
            }else {
                CommentLike commentLike = new CommentLike();
                commentLike.setUserId(userId);
                commentLike.setCommentId(commentId);
                commentLike.setIsLike(1);
                commentLikeService.save(commentLike);
            }
        });
        log.info("定时存放到数据库",commentLikeMap.toString());
        commentLikeMap.clear();
    }



          public void addCommentLike(Long commentId, Long userId) {
           if (commentLikeMap.containsKey(commentId)){
               commentLikeMap.remove(commentId,userId);
           }
           commentLikeMap.put(commentId,userId);
    }
         public void addcomment( Long commentId,Integer likeCount) {
            if (commentMap.containsKey(commentId)){
                commentMap.replace(commentId,likeCount);
            }
            commentMap.put(commentId,likeCount);
        }
}


