package cn.zfizwy.killquestionend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName question_record
 */
@TableName(value ="question_record")
@Data
public class QuestionRecord {
    /**
     * 
     */
    @TableId(type = IdType.ASSIGN_ID)

    private String questionRecordId;

    /**
     * 题库id
     */
    private String questionBankId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 已经做到题目id数组
     */
    private Object accomplishArray;

    /**
     * 以收藏的id数组
     */
    private Object collectArray;

    /**
     * 错误id数组
     */
    private Object errorArray;

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
        QuestionRecord other = (QuestionRecord) that;
        return (this.getQuestionRecordId() == null ? other.getQuestionRecordId() == null : this.getQuestionRecordId().equals(other.getQuestionRecordId()))
            && (this.getQuestionBankId() == null ? other.getQuestionBankId() == null : this.getQuestionBankId().equals(other.getQuestionBankId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getAccomplishArray() == null ? other.getAccomplishArray() == null : this.getAccomplishArray().equals(other.getAccomplishArray()))
            && (this.getCollectArray() == null ? other.getCollectArray() == null : this.getCollectArray().equals(other.getCollectArray()))
            && (this.getErrorArray() == null ? other.getErrorArray() == null : this.getErrorArray().equals(other.getErrorArray()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getQuestionRecordId() == null) ? 0 : getQuestionRecordId().hashCode());
        result = prime * result + ((getQuestionBankId() == null) ? 0 : getQuestionBankId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getAccomplishArray() == null) ? 0 : getAccomplishArray().hashCode());
        result = prime * result + ((getCollectArray() == null) ? 0 : getCollectArray().hashCode());
        result = prime * result + ((getErrorArray() == null) ? 0 : getErrorArray().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", questionRecordId=").append(questionRecordId);
        sb.append(", questionBankId=").append(questionBankId);
        sb.append(", userId=").append(userId);
        sb.append(", accomplishArray=").append(accomplishArray);
        sb.append(", collectArray=").append(collectArray);
        sb.append(", errorArray=").append(errorArray);
        sb.append("]");
        return sb.toString();
    }
}