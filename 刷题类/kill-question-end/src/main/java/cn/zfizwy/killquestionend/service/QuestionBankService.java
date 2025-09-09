package cn.zfizwy.killquestionend.service;


import cn.zfizwy.killquestionend.common.R;
import cn.zfizwy.killquestionend.entity.Peak;
import cn.zfizwy.killquestionend.entity.QuestionBank;
import cn.zfizwy.killquestionend.entity.vo.PeakVO;
import cn.zfizwy.killquestionend.entity.vo.QuestionBankVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author FengXue
* @description 针对表【question_bank】的数据库操作Service
* @createDate 2024-05-14 15:49:28
*/
public interface QuestionBankService extends IService<QuestionBank> {

    IPage<QuestionBank> findQuestionBankByPage(int page, int size,String userId);

    R<Object> delQuestionBankById(List<QuestionBank> questionBankList);

    R<Object> updateQuestionBank(QuestionBank questionBank);

    List<QuestionBankVO> findByState(int state, String userId);

    R<QuestionBankVO> findById(String questionBankId, String userId);

    R<Object> shareBank(String questionBankId, String userId);

    R<Object> peak(Peak peak);

    List<PeakVO> findPeak(String questionBankId);

    R remove(String questionBankId, String userId);

    R<Object> login(String account);
}
