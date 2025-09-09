package cn.zfizwy.killquestionend.service;


import cn.zfizwy.killquestionend.common.R;
import cn.zfizwy.killquestionend.entity.QuestionRecord;
import cn.zfizwy.killquestionend.entity.vo.QuestionBankVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author FengXue
 * @description 针对表【question_record】的数据库操作Service
 * @createDate 2024-05-16 17:17:49
 */
public interface QuestionRecordService extends IService<QuestionRecord> {

    R<Object> collect(String questionId, String questionBankId, String userId, boolean isAuto);


    List<QuestionBankVO> findAll();
}
