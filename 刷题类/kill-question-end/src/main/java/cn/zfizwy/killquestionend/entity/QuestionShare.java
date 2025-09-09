package cn.zfizwy.killquestionend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName question_share
 */
@TableName(value ="question_share")
@Data
public class QuestionShare {
    /**
     * 
     */
    @TableId(type = IdType.ASSIGN_ID)

    private String questionShareId;

    /**
     * 
     */
    private String questionBankId;

    /**
     * 
     */
    private String userId;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        QuestionShare other = (QuestionShare) that;
        return (this.getQuestionShareId() == null ? other.getQuestionShareId() == null : this.getQuestionShareId().equals(other.getQuestionShareId()))
            && (this.getQuestionBankId() == null ? other.getQuestionBankId() == null : this.getQuestionBankId().equals(other.getQuestionBankId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getQuestionShareId() == null) ? 0 : getQuestionShareId().hashCode());
        result = prime * result + ((getQuestionBankId() == null) ? 0 : getQuestionBankId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", questionShareId=").append(questionShareId);
        sb.append(", questionBankId=").append(questionBankId);
        sb.append(", userId=").append(userId);
        sb.append("]");
        return sb.toString();
    }
}