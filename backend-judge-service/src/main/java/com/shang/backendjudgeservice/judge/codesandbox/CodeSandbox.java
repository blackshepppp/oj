package com.shang.backendjudgeservice.judge.codesandbox;

import com.shang.backendmodel.model.codesandbox.ExecuteCodeRequest;
import com.shang.backendmodel.model.codesandbox.ExecuteCodeResponse;

/**
 * 代码沙箱接口定义
 */
public interface CodeSandbox {

    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
