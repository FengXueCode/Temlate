package cn.zfizwy.killquestionend.controller;

import cn.zfizwy.killquestionend.common.R;
import cn.zfizwy.killquestionend.entity.QuestionRecord;
import cn.zfizwy.killquestionend.entity.vo.QuestionBankVO;
import cn.zfizwy.killquestionend.service.QuestionRecordService;
import cn.zfizwy.killquestionend.utils.UserContext;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/questionRecord")
public class QuestionRecordController {
    @Autowired
    QuestionRecordService recordService;

    @GetMapping("/findAll")
    public List<QuestionBankVO> findAll(){
        return recordService.findAll();
    }


    @GetMapping("/reset")
    public R<Object> reset(@RequestParam("questionBankId") String questionBankId) {

        UpdateWrapper<QuestionRecord> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("question_bank_id", questionBankId);
        updateWrapper.eq("user_id", UserContext.get());
        updateWrapper.set("accomplish_array", "[]");
        updateWrapper.set("collect_array", "[]");
        updateWrapper.set("error_array", "[]");
        boolean update = recordService.update(updateWrapper);
        if (update) {
            return new R<>(200, "重置成功", null);
        }

        return new R<>(-1, "重置失败", null);
    }

    @GetMapping("/collect")
    public R<Object> collect(@RequestParam("questionId") String questionId, @RequestParam("questionBankId") String questionBankId,@RequestParam("isAuto") boolean isAuto) {

        return recordService.collect(questionId,questionBankId, UserContext.get(),isAuto);
//
    }

}
