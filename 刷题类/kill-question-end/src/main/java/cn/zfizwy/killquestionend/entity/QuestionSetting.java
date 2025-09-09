package cn.zfizwy.killquestionend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName question_setting
 */
@TableName(value ="question_setting")
@Data
public class QuestionSetting {
    /**
     * 
     */
    @TableId(type = IdType.ASSIGN_ID)

    private String questionSettingId;

    /**
     * 
     */
    private String userId;

    /**
     * 刷题范围 0 全部 1 收藏 2 错误 3 未作
     */
    private Integer scope;

    /**
     * 筛选体系 0 全部 1 单选 2 多选 3 判断 4 填空
     */
    private Integer type;

    /**
     * 刷题模式 0 刷题模式 1 背题模式
     */
    private Integer mode;

    /**
     * 题目乱序 0 关闭 1开启
     */
    private Integer titleChaos;

    /**
     * 选项乱序 0 关闭 1开启
     */
    private Integer optionChaos;

    /**
     * 自动切题 0 关闭 1开启
     */
    private Integer autoSwitch;

    /**
     * 做错自动收藏 0 关闭 1开启
     */
    private Integer autoCollect;

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
        QuestionSetting other = (QuestionSetting) that;
        return (this.getQuestionSettingId() == null ? other.getQuestionSettingId() == null : this.getQuestionSettingId().equals(other.getQuestionSettingId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getScope() == null ? other.getScope() == null : this.getScope().equals(other.getScope()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getMode() == null ? other.getMode() == null : this.getMode().equals(other.getMode()))
            && (this.getTitleChaos() == null ? other.getTitleChaos() == null : this.getTitleChaos().equals(other.getTitleChaos()))
            && (this.getOptionChaos() == null ? other.getOptionChaos() == null : this.getOptionChaos().equals(other.getOptionChaos()))
            && (this.getAutoSwitch() == null ? other.getAutoSwitch() == null : this.getAutoSwitch().equals(other.getAutoSwitch()))
            && (this.getAutoCollect() == null ? other.getAutoCollect() == null : this.getAutoCollect().equals(other.getAutoCollect()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getQuestionSettingId() == null) ? 0 : getQuestionSettingId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getScope() == null) ? 0 : getScope().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getMode() == null) ? 0 : getMode().hashCode());
        result = prime * result + ((getTitleChaos() == null) ? 0 : getTitleChaos().hashCode());
        result = prime * result + ((getOptionChaos() == null) ? 0 : getOptionChaos().hashCode());
        result = prime * result + ((getAutoSwitch() == null) ? 0 : getAutoSwitch().hashCode());
        result = prime * result + ((getAutoCollect() == null) ? 0 : getAutoCollect().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", questionSettingId=").append(questionSettingId);
        sb.append(", userId=").append(userId);
        sb.append(", scope=").append(scope);
        sb.append(", type=").append(type);
        sb.append(", mode=").append(mode);
        sb.append(", titleChaos=").append(titleChaos);
        sb.append(", optionChaos=").append(optionChaos);
        sb.append(", autoSwitch=").append(autoSwitch);
        sb.append(", autoCollect=").append(autoCollect);
        sb.append("]");
        return sb.toString();
    }
}