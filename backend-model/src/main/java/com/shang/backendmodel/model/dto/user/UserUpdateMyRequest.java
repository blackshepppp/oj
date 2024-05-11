package com.shang.backendmodel.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新个人信息请求
 *
 */
@Data
public class UserUpdateMyRequest implements Serializable {

    /**
     * 用户密码更新
     */
    private String password;
    private String password2;


    private static final long serialVersionUID = 1L;
}