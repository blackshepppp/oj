package com.shang.backendmodel.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户评论
 * @TableName user_comments
 */
@TableName(value ="user_comments")
@Data
public class UserComments implements Serializable {
    /**
     * 评论ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 用户ID（外键，引用 user 表的 id）
     */
    private Long userid;
    /**
     * 评论ID
     */
    private Long questionid;

    /**
     * 评论内容
     */
    private String commentcontent;

    /**
     * 创建时间
     */
    private Date createtime;

    /**
     * 更新时间
     */
    private Date updatetime;

    /**
     * 是否删除
     */
    private Integer isdelete;

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