package com.shang.backendmodel.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.shang.backendcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户评论
 * @TableName user_comments
 */

@Data
public class CommentVo  implements Serializable {

    private Long id;
    /**
     * 用户ID（外键，引用 user 表的 id）
     */
    private Long userid;
    /**
     * 题目ID
     */
    private Long questionid;

    /**
     * 用户关联表
     */
    private UserVO userVO;

    /**
     * 评论内容
     */
    private String commentcontent;

    /**
     * 更新时间
     */
    private Date updatetime;

    /**
     * 喜欢数量
     */
    private Integer likescount;

    /**
     * 收藏数量
     */
    private Integer collectscount;

    /**
     * 是否喜欢
     * */
    private Integer isLike;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
