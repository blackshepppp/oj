package com.shang.backendmodel.model.dto.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.shang.backendcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户评论
 * @TableName user_comments
 */

@Data
public class UserCommentDto implements Serializable {
    /**
     * 用户ID（外键，引用 user 表的 id）
     */
    private Long userid;
    /**
     * 题目ID
     */
    private Long questionid;

    /**
     * 评论内容
     */
    private String commentcontent;

    /**
     * 喜欢数量
     */
    private Integer likescount;

    /**
     * 收藏数量
     */
    private Integer collectscount;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}