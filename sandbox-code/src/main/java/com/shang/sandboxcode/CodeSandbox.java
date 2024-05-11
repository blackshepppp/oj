package com.shang.sandboxcode;


import com.shang.sandboxcode.model.ExecuteCodeRequest;
import com.shang.sandboxcode.model.ExecuteCodeResponse;

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
