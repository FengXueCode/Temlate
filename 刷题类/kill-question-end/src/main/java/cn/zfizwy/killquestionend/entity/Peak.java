package cn.zfizwy.killquestionend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName peak
 */
@TableName(value ="peak")
@Data
public class Peak {
    /**
     * 
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String peakId;

    /**
     * 
     */
    private String questionBankId;

    /**
     * 
     */
    private String userId;

    /**
     * 答题用时（按秒计算）
     */
    private Integer time;

    /**
     * 正确数
     */
    private Integer correct;

    /**
     * 总题数
     */
    private Integer total;

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
        Peak other = (Peak) that;
        return (this.getPeakId() == null ? other.getPeakId() == null : this.getPeakId().equals(other.getPeakId()))
            && (this.getQuestionBankId() == null ? other.getQuestionBankId() == null : this.getQuestionBankId().equals(other.getQuestionBankId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getTime() == null ? other.getTime() == null : this.getTime().equals(other.getTime()))
            && (this.getCorrect() == null ? other.getCorrect() == null : this.getCorrect().equals(other.getCorrect()))
            && (this.getTotal() == null ? other.getTotal() == null : this.getTotal().equals(other.getTotal()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getPeakId() == null) ? 0 : getPeakId().hashCode());
        result = prime * result + ((getQuestionBankId() == null) ? 0 : getQuestionBankId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getTime() == null) ? 0 : getTime().hashCode());
        result = prime * result + ((getCorrect() == null) ? 0 : getCorrect().hashCode());
        result = prime * result + ((getTotal() == null) ? 0 : getTotal().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", peakId=").append(peakId);
        sb.append(", questionBankId=").append(questionBankId);
        sb.append(", userId=").append(userId);
        sb.append(", time=").append(time);
        sb.append(", correct=").append(correct);
        sb.append(", total=").append(total);
        sb.append("]");
        return sb.toString();
    }
}