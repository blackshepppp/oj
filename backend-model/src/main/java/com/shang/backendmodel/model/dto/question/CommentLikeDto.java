package com.shang.backendmodel.model.dto.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class CommentLikeDto implements Serializable {
    /**
     * 评论id(外键关联user_comment表的id)）
     */
    private Long commentId;
    /**
     *用户id（外键关联user表的id）
     *
     * */
    private Long userId;


    private Integer isLike;

    private static final long serialVersionUID = 1L;
}
