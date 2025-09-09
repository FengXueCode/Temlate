package cn.zfizwy.killquestionend.service.impl;



import cn.zfizwy.killquestionend.common.R;
import cn.zfizwy.killquestionend.entity.QuestionBank;
import cn.zfizwy.killquestionend.entity.QuestionShare;
import cn.zfizwy.killquestionend.mapper.QuestionShareMapper;
import cn.zfizwy.killquestionend.service.QuestionBankService;
import cn.zfizwy.killquestionend.service.QuestionShareService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author FengXue
* @description 针对表【question_share】的数据库操作Service实现
* @createDate 2024-12-10 09:27:52
*/
@Service
public class QuestionShareServiceImpl extends ServiceImpl<QuestionShareMapper, QuestionShare>
    implements QuestionShareService {
    @Autowired
    private QuestionShareMapper questionShareMapper;
    @Autowired
    private QuestionBankService questionBankService;
    @Override
    public R addQusetionShare(QuestionShare questionShare) {
        //判断是否存在
        QueryWrapper<QuestionShare> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("question_bank_id",questionShare.getQuestionBankId());
        if (questionShareMapper.exists(queryWrapper)) {
            return new R<>(-1, "该题库已存在", null);
        }

        QueryWrapper<QuestionBank> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("question_bank_id",questionShare.getQuestionBankId());
        QuestionBank one = questionBankService.getOne(queryWrapper1);
        if(one.getUserId().equals(questionShare.getUserId())){
            return new R<>(-1, "不能添加自己分享的题库", null);
        }

        boolean save = questionShareMapper.insert(questionShare) > 0;
        return save?new R<>(200, "添加成功", null):new R<>(-1, "添加失败", null);
    }
}




