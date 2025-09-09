package cn.zfizwy.killquestionend.service.impl;


import cn.zfizwy.killquestionend.common.R;
import cn.zfizwy.killquestionend.entity.*;
import cn.zfizwy.killquestionend.entity.vo.QuestionBankVO;
import cn.zfizwy.killquestionend.mapper.QuestionBankMapper;
import cn.zfizwy.killquestionend.mapper.QuestionMapper;
import cn.zfizwy.killquestionend.mapper.QuestionRecordMapper;
import cn.zfizwy.killquestionend.service.QuestionBankService;
import cn.zfizwy.killquestionend.service.QuestionRecordService;
import cn.zfizwy.killquestionend.service.QuestionService;
import cn.zfizwy.killquestionend.service.UserService;
import cn.zfizwy.killquestionend.utils.UserContext;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FengXue
 * @description 针对表【question_record】的数据库操作Service实现
 * @createDate 2024-05-16 17:17:49
 */
@Service
public class QuestionRecordServiceImpl extends ServiceImpl<QuestionRecordMapper, QuestionRecord>
        implements QuestionRecordService {
    @Autowired
    QuestionRecordMapper recordMapper;
    @Autowired
    QuestionBankMapper questionBankMapper;
    @Autowired
    UserService userService;
    @Autowired
    QuestionMapper questionMapper;
    @Override
    public R<Object> collect(String questionId, String questionBankId, String userId, boolean isAuto) {
        QueryWrapper<QuestionRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("question_bank_id", questionBankId);
        QuestionRecord record = recordMapper.selectOne(queryWrapper);
        List<String> collectArray = JSON.parseArray((String) record.getCollectArray(), String.class);
        int i = collectArray.indexOf(questionId);
        if (i == -1) {
            collectArray.add(questionId);
        } else {
            if (!isAuto) {
                collectArray.remove(i);
                record.setCollectArray(JSON.toJSONString(collectArray));
                recordMapper.updateById(record);
                return new R<>(200, "取消收藏成功", "取消收藏成功");
            }
        }
        record.setCollectArray(JSON.toJSONString(collectArray));
        int update = recordMapper.updateById(record);
        if (update > 0) {
            return new R<>(200, "收藏成功", "收藏成功");
        }
        return new R<>(-1, "收藏失败", null);
    }

    @Override
    public List<QuestionBankVO> findAll() {
        LambdaQueryWrapper<QuestionRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(QuestionRecord::getUserId, UserContext.get());
        List<QuestionRecord> questionRecords = recordMapper.selectList(queryWrapper);
        List<QuestionBankVO> questionBankVOList = new ArrayList<>();
        for (QuestionRecord questionRecord : questionRecords) {
            QuestionBank questionBank = questionBankMapper.selectById(questionRecord.getQuestionBankId());

            QuestionBankVO questionBankVO = new QuestionBankVO();
            questionBankVO.setQuestionBank(questionBank);

            questionBankVO.setQuestionRecord(questionRecord);
            User user = userService.getById(questionBank.getUserId());

            questionBankVO.setPortrait(user.getPortrait());

            QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<Question>().eq("question_bank_id", questionBank.getQuestionBankId());
            List<Question> list = questionMapper.selectList(questionQueryWrapper);
            questionBankVO.setTotal(list.size());

            questionBankVOList.add(questionBankVO);
        }
       if(questionRecords.isEmpty()){
           QuestionRecord questionRecord = new QuestionRecord();
           questionRecord.setQuestionBankId("1964970914432954369");
           questionRecord.setUserId(UserContext.get());
           questionRecord.setAccomplishArray("[]");
           questionRecord.setCollectArray("[]");
           questionRecord.setErrorArray("[]");
           recordMapper.insert(questionRecord);
           QuestionBank questionBank = questionBankMapper.selectById("1964970914432954369");
           questionBankVOList.add(new QuestionBankVO(questionBank, questionRecord, userService.getById(questionBank.getUserId()).getPortrait(), 0));
       }

        return questionBankVOList;
    }


}




