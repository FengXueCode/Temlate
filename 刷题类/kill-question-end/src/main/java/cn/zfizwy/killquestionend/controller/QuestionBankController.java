package cn.zfizwy.killquestionend.controller;


import cn.zfizwy.killquestionend.common.R;
import cn.zfizwy.killquestionend.entity.Peak;
import cn.zfizwy.killquestionend.entity.QuestionBank;
import cn.zfizwy.killquestionend.entity.QuestionRecord;
import cn.zfizwy.killquestionend.entity.User;
import cn.zfizwy.killquestionend.entity.vo.PeakVO;
import cn.zfizwy.killquestionend.entity.vo.QuestionBankVO;
import cn.zfizwy.killquestionend.service.QuestionBankService;
import cn.zfizwy.killquestionend.service.QuestionRecordService;
import cn.zfizwy.killquestionend.service.UserService;
import cn.zfizwy.killquestionend.utils.UserContext;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/questionBank")
public class QuestionBankController {
    @Autowired
    QuestionBankService questionBankService;

    @Autowired
    QuestionRecordService questionRecordService;

    @Autowired
    UserService userService;

    /**
     * 用户登录
     *
     * @param account  用户账号
     * @return 返回登录结果，包含状态码、消息和用户信息
     */
    @GetMapping("/login")
    public R<Object> login(@RequestParam("account") String account) {
       return questionBankService.login(account);
    }

    /**
     * 创建题库
     *
     * @param questionBank 题库信息
     * @return 返回创建结果，包含状态码、消息和创建的题库信息
     */
    @PostMapping("/create")
    public R<QuestionBank> createQuestionBank(@RequestBody QuestionBank questionBank) {
        questionBank.setUserId(UserContext.get());
        boolean save = questionBankService.save(questionBank);
        if (save) {
            // 创建题库记录
            QuestionRecord questionRecord = new QuestionRecord();
            questionRecord.setQuestionBankId(questionBank.getQuestionBankId());
            questionRecord.setUserId(questionBank.getUserId());
            questionRecord.setAccomplishArray("[]");
            questionRecord.setCollectArray("[]");
            questionRecord.setErrorArray("[]");
            questionRecordService.save(questionRecord);
            return new R<>(200, "保存成功", questionBank);
        } else {
            return new R<>(-1, "保存失败", null);
        }
    }

    /**
     * 根据题库ID删除题库
     *
     * @param QuestionBankList 题库列表
     * @return 返回删除结果
     */
    @PostMapping("/delQuestionBankById")
    public R<Object> delQuestionBankById(@RequestBody List<QuestionBank> QuestionBankList) {
        return questionBankService.delQuestionBankById(QuestionBankList);
    }

    /**
     * 更新题库信息
     *
     * @return 返回更新结果
     */
    @PostMapping("/updateQuestionBank")
    public R<Object> updateQuestionBank(@RequestBody QuestionBank questionBank) {
        questionBank.setUserId(UserContext.get());
        return questionBankService.updateQuestionBank(questionBank);
    }

    /**
     * 分页查询题库
     *
     * @param page 页码
     * @param size 每页数量
     * @return 返回分页查询结果
     */
    @GetMapping("/findQuestionBankByPage")
    public IPage<QuestionBank> findQuestionBankByPage(@RequestParam("pageNum") int page, @RequestParam("pageSize") int size) {
        return questionBankService.findQuestionBankByPage(page, size, UserContext.get());
    }

    /**
     * 根据状态查询题库
     *
     * @param state 题库状态
     * @return 返回查询结果列表
     */
    @GetMapping("/findByState")
    public List<QuestionBankVO> findByState(@RequestParam("state") int state) {

        return questionBankService.findByState(state, UserContext.get());
    }



    /**
     * 根据题库ID查询题库信息
     *
     * @param questionBankId 题库ID
     * @return 返回查询的题库信息
     */
    @GetMapping("/findById")
    public R<QuestionBankVO> findById(@RequestParam("questionBankId") String questionBankId) {

        return questionBankService.findById(questionBankId, UserContext.get());
    }

    @GetMapping("/shareBank")
    public R<Object> shareBank(@RequestParam("questionBankId") String questionBankId) {

        return questionBankService.shareBank(questionBankId, UserContext.get());
    }

    @PostMapping("/peak")
    public R<Object> peak(@RequestBody Peak peak) {
        return questionBankService.peak(peak);
    }

    @GetMapping("/findPeak")
    public List<PeakVO> findPeak(@RequestParam("questionBankId") String questionBankId) {
        return questionBankService.findPeak(questionBankId);
    }

    @GetMapping("/remove")
    public R remove(@RequestParam("questionBankId") String questionBankId) {

        return questionBankService.remove(questionBankId, UserContext.get());
    }

}
