package cn.zfizwy.killquestionend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName question_bank
 */
@TableName(value ="question_bank")
@Data
public class QuestionBank {
    /**
     * 题库id
     */
    @TableId(type = IdType.ASSIGN_ID)

    private String questionBankId;

    /**
     * 题库名称
     */
    private String questionBankName;

    /**
     * 作者id
     */
    private String userId;

    /**
     * 作者名称
     */
    private String author;

    /**
     * 0 私人 1 共享
     */
    private Integer type;

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
        QuestionBank other = (QuestionBank) that;
        return (this.getQuestionBankId() == null ? other.getQuestionBankId() == null : this.getQuestionBankId().equals(other.getQuestionBankId()))
            && (this.getQuestionBankName() == null ? other.getQuestionBankName() == null : this.getQuestionBankName().equals(other.getQuestionBankName()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getAuthor() == null ? other.getAuthor() == null : this.getAuthor().equals(other.getAuthor()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getQuestionBankId() == null) ? 0 : getQuestionBankId().hashCode());
        result = prime * result + ((getQuestionBankName() == null) ? 0 : getQuestionBankName().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getAuthor() == null) ? 0 : getAuthor().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", questionBankId=").append(questionBankId);
        sb.append(", questionBankName=").append(questionBankName);
        sb.append(", userId=").append(userId);
        sb.append(", author=").append(author);
        sb.append(", type=").append(type);
        sb.append("]");
        return sb.toString();
    }
}