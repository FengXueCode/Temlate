package cn.zfizwy.xfmahjongend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName friend
 */
@TableName(value ="friend")
@Data
public class Friend {
    /**
     * 关系编号
     */
    @TableId
    private String friendId;

    /**
     * 用户编号
     */
    private String userId;

    /**
     * 好友编号
     */
    private String toUserId;

    /**
     * 别名
     */
    private String remark;

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
        Friend other = (Friend) that;
        return (this.getFriendId() == null ? other.getFriendId() == null : this.getFriendId().equals(other.getFriendId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getToUserId() == null ? other.getToUserId() == null : this.getToUserId().equals(other.getToUserId()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getFriendId() == null) ? 0 : getFriendId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getToUserId() == null) ? 0 : getToUserId().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", friendId=").append(friendId);
        sb.append(", userId=").append(userId);
        sb.append(", toUserId=").append(toUserId);
        sb.append(", remark=").append(remark);
        sb.append("]");
        return sb.toString();
    }
}