package cn.zfizwy.xfmahjongend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 
 * @TableName room_user
 */
@TableName(value ="room_user")
@Data
public class RoomUser {
    /**
     * 
     */
    private String roomId;

    /**
     * 
     */
    private String userId;

    /**
     * 用户金额
     */
    private BigDecimal money;

    /**
     * 0 已结算 1牌局中
     */
    private Integer status;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 茶水金额
     */
    private BigDecimal tea;

    /**
     * 用户头像
     */
    private String portrait;

    /**
     * 0 东 1 南 2 西 3 北
     */
    private Integer location;

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
        RoomUser other = (RoomUser) that;
        return (this.getRoomId() == null ? other.getRoomId() == null : this.getRoomId().equals(other.getRoomId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getMoney() == null ? other.getMoney() == null : this.getMoney().equals(other.getMoney()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getNickname() == null ? other.getNickname() == null : this.getNickname().equals(other.getNickname()))
            && (this.getTea() == null ? other.getTea() == null : this.getTea().equals(other.getTea()))
            && (this.getPortrait() == null ? other.getPortrait() == null : this.getPortrait().equals(other.getPortrait()))
            && (this.getLocation() == null ? other.getLocation() == null : this.getLocation().equals(other.getLocation()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getRoomId() == null) ? 0 : getRoomId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getMoney() == null) ? 0 : getMoney().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getNickname() == null) ? 0 : getNickname().hashCode());
        result = prime * result + ((getTea() == null) ? 0 : getTea().hashCode());
        result = prime * result + ((getPortrait() == null) ? 0 : getPortrait().hashCode());
        result = prime * result + ((getLocation() == null) ? 0 : getLocation().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", roomId=").append(roomId);
        sb.append(", userId=").append(userId);
        sb.append(", money=").append(money);
        sb.append(", status=").append(status);
        sb.append(", nickname=").append(nickname);
        sb.append(", tea=").append(tea);
        sb.append(", portrait=").append(portrait);
        sb.append(", location=").append(location);
        sb.append("]");
        return sb.toString();
    }
}