package cn.zfizwy.killquestionend.service;

import cn.zfizwy.killquestionend.common.R;
import cn.zfizwy.killquestionend.entity.Question;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author FengXue
* @description 针对表【question】的数据库操作Service
* @createDate 2024-05-15 16:01:50
*/
public interface QuestionService extends IService<Question> {

    R<Object> delete(List<Question> list);

    R<Object> update(Question question);


    R<Object> judge(String questionId, String userId, String answer, Boolean peak,String questionBankId);

    List<Question> findAll(String questionBankId,String userId);

    List<Question> findPeak(String questionBankId);

    R upload(MultipartFile file, String questionBankId);
}
