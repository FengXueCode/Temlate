package cn.zfizwy.killquestionend.entity.vo;

import cn.zfizwy.killquestionend.entity.QuestionBank;
import cn.zfizwy.killquestionend.entity.QuestionRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QuestionBankVO {
    private QuestionBank questionBank;//刷题库
    private QuestionRecord questionRecord;//刷题记录
    private String portrait;//用户头像
    private int total;//题库总量

}
