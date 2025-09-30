package cn.zfizwy.xfmahjongend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName room_ending
 */
@TableName(value ="room_ending")
@Data
public class RoomEnding {
    /**
     * 房间结局
     */
    @TableId
    private String roomEndingId;

    /**
     * 
     */
    private String roomId;

    /**
     * 
     */
    private String userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 0 东 1 南 2 西 3 北
     */
    private Integer location;

    /**
     * 用户金额
     */
    private BigDecimal money;

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
        RoomEnding other = (RoomEnding) that;
        return (this.getRoomEndingId() == null ? other.getRoomEndingId() == null : this.getRoomEndingId().equals(other.getRoomEndingId()))
            && (this.getRoomId() == null ? other.getRoomId() == null : this.getRoomId().equals(other.getRoomId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getLocation() == null ? other.getLocation() == null : this.getLocation().equals(other.getLocation()))
            && (this.getMoney() == null ? other.getMoney() == null : this.getMoney().equals(other.getMoney()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getRoomEndingId() == null) ? 0 : getRoomEndingId().hashCode());
        result = prime * result + ((getRoomId() == null) ? 0 : getRoomId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getLocation() == null) ? 0 : getLocation().hashCode());
        result = prime * result + ((getMoney() == null) ? 0 : getMoney().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", roomEndingId=").append(roomEndingId);
        sb.append(", roomId=").append(roomId);
        sb.append(", userId=").append(userId);
        sb.append(", createTime=").append(createTime);
        sb.append(", location=").append(location);
        sb.append(", money=").append(money);
        sb.append("]");
        return sb.toString();
    }
}