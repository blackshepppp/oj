package com.shang.backendmodel.model.dto.question;

import com.baomidou.mybatisplus.annotation.TableField;
import com.shang.backendcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户评论
 * @TableName user_comments
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserCommentGetDto extends PageRequest implements Serializable {

    /**
     * 题目ID
     */
    private Long questionid;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}