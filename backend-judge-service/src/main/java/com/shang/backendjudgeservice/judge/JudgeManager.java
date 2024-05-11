package com.shang.backendjudgeservice.judge;

import com.shang.backendjudgeservice.judge.strategy.DefaultJudgeStrategy;
import com.shang.backendjudgeservice.judge.strategy.JavaLanguageJudgeStrategy;
import com.shang.backendjudgeservice.judge.strategy.JudgeStrategy;
import com.shang.backendjudgeservice.judge.strategy.JudgeContext;
import com.shang.backendmodel.model.codesandbox.JudgeInfo;
import com.shang.backendmodel.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
