package cn.zfizwy.killquestionend.service;


import cn.zfizwy.killquestionend.common.R;
import cn.zfizwy.killquestionend.entity.QuestionShare;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author FengXue
* @description 针对表【question_share】的数据库操作Service
* @createDate 2024-12-10 09:35:44
*/
public interface QuestionShareService extends IService<QuestionShare> {
    R addQusetionShare(QuestionShare questionShare);
}
