package com.shang.backendjudgeservice.judge.codesandbox;

import com.shang.backendjudgeservice.judge.codesandbox.impl.ExampleCodeSandbox;
import com.shang.backendjudgeservice.judge.codesandbox.impl.RemoteCodeSandbox;
import com.shang.backendjudgeservice.judge.codesandbox.impl.ThirdPartyCodeSandbox;
import lombok.extern.slf4j.Slf4j;

/**
 * 代码沙箱工厂（根据字符串参数创建指定的代码沙箱实例）
 */
@Slf4j
public class CodeSandboxFactory {

    /**
     * 创建代码沙箱示例
     *
     * @param type 沙箱类型
     * @return
     */
    public static CodeSandbox newInstance(String type) {
        log.info(type);
        switch (type) {
            case "example":
                return new ExampleCodeSandbox();
            case "remote":
                return new RemoteCodeSandbox();
            case "thirdParty":
                return new ThirdPartyCodeSandbox();
            default:
                return new ExampleCodeSandbox();
        }
    }
}
