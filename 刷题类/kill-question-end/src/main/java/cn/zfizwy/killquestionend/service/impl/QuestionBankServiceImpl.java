package cn.zfizwy.killquestionend.service.impl;


import cn.zfizwy.killquestionend.common.R;
import cn.zfizwy.killquestionend.entity.*;
import cn.zfizwy.killquestionend.entity.vo.PeakVO;
import cn.zfizwy.killquestionend.entity.vo.QuestionBankVO;
import cn.zfizwy.killquestionend.entity.vo.UserVO;
import cn.zfizwy.killquestionend.mapper.QuestionBankMapper;
import cn.zfizwy.killquestionend.mapper.QuestionShareMapper;
import cn.zfizwy.killquestionend.service.*;
import cn.zfizwy.killquestionend.utils.TokenUtil;
import cn.zfizwy.killquestionend.utils.UserContext;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FengXue
 * @description 针对表【question_bank】的数据库操作Service实现
 * @createDate 2024-05-14 15:49:28
 */
@Service
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBankMapper, QuestionBank>
        implements QuestionBankService {
    @Autowired
    QuestionBankMapper questionBankMapper;
    @Autowired
    QuestionRecordService questionRecordService;
    @Autowired
    QuestionService questionService;
    @Autowired
    PeakService peakService;
    @Autowired
    QuestionShareMapper questionShareMapper;

    @Autowired
    UserService userService;

    @Override
    public IPage<QuestionBank> findQuestionBankByPage(int page, int size, String userId) {
        Page<QuestionBank> questionBankPage = new Page<>(page, size);
        QueryWrapper<QuestionBank> queryWrapper = new QueryWrapper<QuestionBank>().eq("user_id", userId);
        IPage<QuestionBank> pageList = questionBankMapper.selectPage(questionBankPage, queryWrapper);
        return pageList;
    }

    @Override
    public R<Object> delQuestionBankById(List<QuestionBank> questionBankList) {
        List<String> toDeleteIds = new ArrayList<>(); // 用于保存需要删除的 Id
        for (QuestionBank QuestionBank : questionBankList) {
            toDeleteIds.add(QuestionBank.getQuestionBankId()); // 将需要删除的 QuestionBankId 加入 toDeleteIds
        }
        // 删除需要删除的 QuestionBank
        int flag = questionBankMapper.deleteBatchIds(toDeleteIds);

        if (flag > 0) {
            return new R<>(200, "删除成功", null);
        }
        return new R<>(-1, "删除失败", null);
    }

    @Override
    public R<Object> updateQuestionBank(QuestionBank questionBank) {
        int flag = questionBankMapper.update(questionBank, new QueryWrapper<QuestionBank>().eq("question_bank_id", questionBank.getQuestionBankId()));

        if (flag > 0) {
            return new R<>(200, "更新成功", null);
        }
        return new R<>(-1, "更新失败", null);
    }

    @Override
    public List<QuestionBankVO> findByState(int state, String userId) {
        QueryWrapper<QuestionBank> queryWrapper = new QueryWrapper<QuestionBank>().eq("type", state);
        if (state == 0) {
            queryWrapper.eq("user_id", userId);
        }
        List<QuestionBank> questionBanks = questionBankMapper.selectList(queryWrapper);
        if(state == 0){
            QueryWrapper<QuestionShare> questionShareQueryWrapper = new QueryWrapper<>();
            questionShareQueryWrapper.eq("user_id", userId);
            List<QuestionShare> questionShares = questionShareMapper.selectList(questionShareQueryWrapper);
            for (QuestionShare questionShare : questionShares) {
                questionBanks.add(questionBankMapper.selectById(questionShare.getQuestionBankId()));
            }
        }

        List<QuestionBankVO> questionBankVOList = new ArrayList<>();
        for (QuestionBank questionBank : questionBanks) {
            QuestionBankVO questionBankVO = new QuestionBankVO();
            questionBankVO.setQuestionBank(questionBank);
            QueryWrapper<QuestionRecord> questionRecordQueryWrapper = new QueryWrapper<QuestionRecord>().eq("question_bank_id", questionBank.getQuestionBankId());
            questionRecordQueryWrapper.eq("user_id", userId);
            QuestionRecord record = questionRecordService.getOne(questionRecordQueryWrapper);
            questionBankVO.setQuestionRecord(record);
            User user = userService.getById(questionBank.getUserId());
            questionBankVO.setPortrait(user.getPortrait());
            QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<Question>().eq("question_bank_id", questionBank.getQuestionBankId());
            List<Question> list = questionService.list(questionQueryWrapper);
            questionBankVO.setTotal(list.size());
            questionBankVOList.add(questionBankVO);
        }
        return questionBankVOList;
    }

    @Override
    public R<QuestionBankVO> findById(String questionBankId, String userId) {
        QuestionBank questionBank = questionBankMapper.selectById(questionBankId);
        QueryWrapper<QuestionRecord> questionRecordQueryWrapper = new QueryWrapper<QuestionRecord>().eq("user_id", userId).eq("question_bank_id",questionBankId);
        QuestionRecord record = questionRecordService.getOne(questionRecordQueryWrapper);
        if (record == null) {
            record = new QuestionRecord();
            record.setUserId(userId);
            record.setQuestionBankId(questionBankId);
            record.setErrorArray("[]");
            record.setAccomplishArray("[]");
            record.setCollectArray("[]");
            questionRecordService.save(record);
        }
        User user = userService.getById(questionBank.getUserId());

        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<Question>().eq("question_bank_id", questionBank.getQuestionBankId());
        List<Question> list = questionService.list(questionQueryWrapper);
        return new R<>(200, "查询成功", new QuestionBankVO(questionBank, record,  user.getPortrait(), list.size()));
    }

    @Override
    public R<Object> shareBank(String questionBankId, String userId) {
        UpdateWrapper<QuestionBank> updateWrapper = new UpdateWrapper<QuestionBank>().set("type", 1).eq("question_bank_id", questionBankId);
        int update = questionBankMapper.update(updateWrapper);
        if (update > 0) {
            return new R<>(200, "分享成功", null);
        }

        return new R<>(-1, "分享失败", null);
    }

    @Override
    public R<Object> peak(Peak peak) {
        peak.setUserId(UserContext.get());
        boolean save = peakService.save(peak);
        if (save) {
            return new R<>(200, "保存成功", null);
        }
        return new R<>(-1, "保存失败", null);

    }

    @Override
    public List<PeakVO> findPeak(String questionBankId) {
        QueryWrapper<Peak> queryWrapper = new QueryWrapper<Peak>().eq("question_bank_id", questionBankId);
        queryWrapper.orderByDesc("correct");
        queryWrapper.orderByAsc("time");

        List<Peak> list = peakService.list(queryWrapper);
        List<PeakVO> arrayList = new ArrayList<>();
        list.forEach(peak -> {
            User user = userService.getById(peak.getUserId());

           arrayList.add(new PeakVO(peak, user));
        });
        return  arrayList;
    }

    @Override
    public R remove(String questionBankId, String userId) {
        QueryWrapper<QuestionBank> wrapper = new QueryWrapper<>();
        wrapper.eq("question_bank_id", questionBankId);
        QuestionBank questionBank = questionBankMapper.selectOne(wrapper);
        if (questionBank.getUserId().equals(userId)) {
            return questionBankMapper.delete(wrapper) > 0 ? new R<>(200, "删除成功", null) : new R<>(-1, "删除失败", null);
        }else{
            int delete = questionShareMapper.delete(new QueryWrapper<QuestionShare>().eq("question_bank_id", questionBankId).eq("user_id", userId));
            return  delete>0?new R<>(200,"删除成功",null):new R<>(-1,"删除失败",null);
        }

    }

    @Override
    public R<Object> login(String account) {
        User user = userService.getById(account);
        if(user == null){
            return new R<>(-1, "请到小程序中查看编号", null);
        }
        String token = TokenUtil.sign(user.getUserId());
        UserVO userVO = new UserVO();
        userVO.setNickName(user.getNickName());
        userVO.setPortrait(user.getPortrait());
        userVO.setToken(token);
//            redisTemplate.opsForValue().set(token, exists.getUserId());
        return new R<>(200, "登录成功", userVO);
    }


}




