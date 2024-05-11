package com.shang.backendquestionservice.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shang.backendcommon.common.BaseResponse;
import com.shang.backendcommon.common.ErrorCode;
import com.shang.backendcommon.common.ResultUtils;
import com.shang.backendcommon.exception.ThrowUtils;
import com.shang.backendmodel.model.dto.question.CommentLikeDto;
import com.shang.backendmodel.model.dto.question.UserCommentDto;
import com.shang.backendmodel.model.dto.question.UserCommentGetDto;
import com.shang.backendmodel.model.entity.CommentLike;
import com.shang.backendmodel.model.entity.User;
import com.shang.backendmodel.model.entity.UserComments;
import com.shang.backendmodel.model.vo.CommentVo;
import com.shang.backendquestionservice.mapper.CommentLikeMapper;
import com.shang.backendquestionservice.service.UserCommentsService;
import com.shang.backendserviceclient.service.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/")
@Slf4j
public class UserCommentsController {


   static final String key="comment:";

    @Resource
    private UserCommentsService userCommentsService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private UserFeignClient userFClient;

    @Resource
    ScheduledCommmet scheduledCommmet;

    @Resource
    private CommentLikeMapper commentLikeMapper;

    @PostMapping("/add/comment")
    public BaseResponse addUserComments(@RequestBody UserCommentDto userCommentDto) {
        UserComments userComments = new UserComments();
        if (userCommentDto.getQuestionid() == null ){
            return ResultUtils.error(400,"参数错误,发送失败");
        }
        BeanUtil.copyProperties(userCommentDto,userComments);
        boolean save = userCommentsService.insert(userComments);
        if (!save){
            return ResultUtils.error(400,"发送失败");
        }
        return ResultUtils.success("发布成功");
    }

    @PostMapping("/get/comment")
    public BaseResponse getUserComments(@RequestBody UserCommentGetDto userCommentDto, HttpServletRequest request ){
        if (userCommentDto.getQuestionid() == null){
            return ResultUtils.error(400,"参数错误");
        }
        long current = userCommentDto.getCurrent();
        long size = userCommentDto.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<UserComments> Page = userCommentsService.page(new Page<>(current, size),
                    userCommentsService.getQueryWrapper(userCommentDto));
        Page<CommentVo> Page2 = userCommentsService.getQuestionVOPage(Page,request);
        if (Page2 == null){
            return ResultUtils.error(400,"该评论不存在");
        }
        //放在缓存里面
        List<CommentVo> records = Page2.getRecords();
        Set<Long> set=new HashSet<>();
        final int[] i = {0};
        records.forEach(
                commentVo -> {
                   Integer likescount = commentVo.getLikescount();
                    Long id = commentVo.getId();//当前评论的id
                    //存放点赞数到redis
                    redisTemplate.opsForHash().put(key+id,id.toString(),likescount);
                    // todo 记得设置过期时间
                    LambdaQueryWrapper<CommentLike> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(CommentLike::getCommentId,id);
                    User loginUser = userFClient.getLoginUser(request);//拿到当前的用户
                    queryWrapper.eq(CommentLike::getUserId,loginUser.getId());
                    queryWrapper.eq(CommentLike::getIsLike,1);
                    List<CommentLike> commentLikes = commentLikeMapper.selectList(queryWrapper);//最多一条
                    //结构评论的id 比上 当前用户id
                    if(commentLikes.size()==1){
                        redisTemplate.opsForHash().put(key+loginUser.getId(),id.toString(),loginUser.getId().toString());
                        CommentVo commentVo1 = records.get(i[0]);
                        commentVo1.setIsLike(1);
                        commentVo1.setUserid(loginUser.getId());
                        records.set(i[0],commentVo1);
                    }else{
                        CommentVo commentVo1 = records.get(i[0]);
                        commentVo1.setIsLike(0);
                        commentVo1.setUserid(loginUser.getId());
                        records.set(i[0],commentVo1);
                    }
                    i[0]++;
                }
        );

        return ResultUtils.success(Page2);
    }

    @DeleteMapping("/delete/comment")
    public BaseResponse deleteUserComments(@RequestBody UserCommentDto userCommentDto){
        if (userCommentDto.getQuestionid() == null){
            return ResultUtils.error(400,"参数错误");
        }
        boolean b = userCommentsService.remove(new LambdaQueryWrapper<UserComments>().eq(UserComments::getQuestionid, userCommentDto.getQuestionid()));
        if (!b){
            return ResultUtils.error(400,"删除失败");
        }
        return ResultUtils.success("删除成功");
    }

//    @PostMapping("/update/comment/like")
//    public BaseResponse updateUserCommentsLike (@RequestBody CommentLikeDto commentLikeDto){
//        Gson gson = new Gson();
//        /**
//         * 我采用redis评论id对应的userid（用户）集合的Hash类的操作
//         * */
//        if (commentLikeDto.getCommentId() == null){
//            return ResultUtils.error(400,"参数错误");
//        }
//        if (commentLikeDto.getUserId() == null){
//            return ResultUtils.error(400,"参数错误");
//        }
//        Set<Long> set=null;
//        //先转为JSON格式的Set集合
//        String map   =(String) redisTemplate.opsForHash().get("commentLike", commentLikeDto.getCommentId().toString());
//        try {
//            // 使用Gson的fromJson方法将JSON数组解析为Set
//            Type setType = new TypeToken<Set<Long>>(){}.getType();
//            set = gson.fromJson(map, setType);
//            if (set==null){
//                set =new HashSet<>();
//                set.add(commentLikeDto.getUserId());
//                String jsonStr = gson.toJson(set);
//                log.info(jsonStr);
//                if (commentLikeDto.getIsLike()==1){
//                redisTemplate.opsForHash().put("commentLike",commentLikeDto.getCommentId().toString(),jsonStr);
//                redisTemplate.expire("commentLike",1, TimeUnit.SECONDS);
//                    return ResultUtils.success("点赞成功");
//                }else{
//                    //取消点赞
//                    // TODO: 2024/4/23  数据库删除
//                    return ResultUtils.success("取消点赞");
//                }
//            }
//            // 如果需要，可以将Set转换为不可变的Set
//            set = Collections.unmodifiableSet(set);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (set!=null){
//            Set<Long> newSet = new HashSet<>(set);
//        if (newSet.contains(commentLikeDto.getUserId())){
//            log.info("当前set的值"+set.toString());
//            newSet.remove(commentLikeDto.getUserId());
//            String jsonStr =gson.toJson(newSet);
//            redisTemplate.opsForHash().put("commentLike",commentLikeDto.getCommentId().toString(),jsonStr);
//            redisTemplate.expire("commentLike",1, TimeUnit.SECONDS);
//            //TOdo 数据库的操作
//            return ResultUtils.success("取消点赞");
//        }
//        newSet.add(commentLikeDto.getUserId());
//        String jsonStr = gson.toJson(newSet);
//        log.info(jsonStr);
//        redisTemplate.opsForHash().put("commentLike",commentLikeDto.getCommentId().toString(),jsonStr);
//        redisTemplate.expire("commentLike",1, TimeUnit.SECONDS);
//        return ResultUtils.success("点赞成功");
//        }
//        return ResultUtils.error(400,"点赞失败");
//    }
    @PostMapping("/update/comment/like")
    public BaseResponse updateUserCommentsLike (@RequestBody CommentLikeDto commentLikeDto){
        Gson gson = new Gson();
        /**
         * 我采用redis评论id对应的userid（用户）集合的Hash类的操作
         * */
        if (commentLikeDto.getCommentId() == null){
            return ResultUtils.error(400,"参数错误");
        }
        if (commentLikeDto.getUserId() == null){
            return ResultUtils.error(400,"参数错误");
        }
        if (commentLikeDto.getIsLike()==1){
            //todo 可以用redis自增的方法，以后再做
            //点赞
            redisTemplate.opsForHash().put("commentLike",commentLikeDto.getCommentId().toString(),commentLikeDto.getUserId().toString());
            //放到定时的任务中
            scheduledCommmet.addCommentLike(commentLikeDto.getCommentId(), commentLikeDto.getUserId());
            Integer o = (Integer) redisTemplate.opsForHash().get(key + commentLikeDto.getCommentId(), commentLikeDto.getCommentId().toString());
            log.info("当前点赞数"+o);
            o++;
            redisTemplate.opsForHash().put(key + commentLikeDto.getCommentId(), commentLikeDto.getCommentId().toString(), o);
            scheduledCommmet.addcomment(commentLikeDto.getCommentId(),o);
            Integer a = (Integer) redisTemplate.opsForHash().get(key + commentLikeDto.getCommentId(), commentLikeDto.getCommentId().toString());
            log.info("当前点赞数"+a);
            return ResultUtils.success("点赞成功");
        }else{
            //取消点赞
            redisTemplate.opsForHash().delete("commentLike",commentLikeDto.getCommentId().toString(),commentLikeDto.getUserId().toString());
            Integer o = (Integer) redisTemplate.opsForHash().get(key + commentLikeDto.getCommentId(), commentLikeDto.getCommentId().toString());
            log.info("当前点赞数"+o);
            //todo 再来一个定时任务记录删除或者我们把取消点赞改为json格式true以Map形式储存
            //这边我就先直接删除了

            if (o==0){
                throw new RuntimeException("点赞数不能为负数");
            }
            o--;
            LambdaQueryWrapper<CommentLike> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(CommentLike::getCommentId,commentLikeDto.getCommentId());
            lambdaQueryWrapper.eq(CommentLike::getUserId,commentLikeDto.getUserId());
            commentLikeMapper.delete(lambdaQueryWrapper);
            redisTemplate.opsForHash().put(key + commentLikeDto.getCommentId(), commentLikeDto.getCommentId().toString(), o);
            Integer a = (Integer) redisTemplate.opsForHash().get(key + commentLikeDto.getCommentId(), commentLikeDto.getCommentId().toString());
            log.info("当前点赞数"+a);
            scheduledCommmet.addCommentLike(commentLikeDto.getCommentId(), commentLikeDto.getUserId());
            scheduledCommmet.addcomment(commentLikeDto.getCommentId(),o);
            return ResultUtils.success("取消点赞");
        }
    }
}
