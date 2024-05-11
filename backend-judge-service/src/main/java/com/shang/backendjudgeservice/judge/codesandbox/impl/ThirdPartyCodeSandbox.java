package com.shang.backendjudgeservice.judge.codesandbox.impl;

import com.shang.backendjudgeservice.judge.codesandbox.CodeSandbox;
import com.shang.backendmodel.model.codesandbox.ExecuteCodeRequest;
import com.shang.backendmodel.model.codesandbox.ExecuteCodeResponse;

public class ThirdPartyCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
