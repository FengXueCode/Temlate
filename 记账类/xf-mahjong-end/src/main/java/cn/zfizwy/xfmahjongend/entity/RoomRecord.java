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
 * @TableName room_record
 */
@TableName(value ="room_record")
@Data
public class RoomRecord {
    /**
     * 房间记录id
     */
    @TableId
    private String roomRecordId;

    /**
     * 房间id
     */
    private String roomId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 赢家id
     */
    private String winner;

    /**
     * 败家id
     */
    private String loser;

    /**
     * 金额
     */
    private BigDecimal money;

    /**
     * 茶水费
     */
    private BigDecimal tea;

    /**
     * 0 转账 1 结算
     */
    private Integer recordType;

    /**
     * 结算者id
     */
    private String settlement;

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
        RoomRecord other = (RoomRecord) that;
        return (this.getRoomRecordId() == null ? other.getRoomRecordId() == null : this.getRoomRecordId().equals(other.getRoomRecordId()))
            && (this.getRoomId() == null ? other.getRoomId() == null : this.getRoomId().equals(other.getRoomId()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getWinner() == null ? other.getWinner() == null : this.getWinner().equals(other.getWinner()))
            && (this.getLoser() == null ? other.getLoser() == null : this.getLoser().equals(other.getLoser()))
            && (this.getMoney() == null ? other.getMoney() == null : this.getMoney().equals(other.getMoney()))
            && (this.getTea() == null ? other.getTea() == null : this.getTea().equals(other.getTea()))
            && (this.getRecordType() == null ? other.getRecordType() == null : this.getRecordType().equals(other.getRecordType()))
            && (this.getSettlement() == null ? other.getSettlement() == null : this.getSettlement().equals(other.getSettlement()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getRoomRecordId() == null) ? 0 : getRoomRecordId().hashCode());
        result = prime * result + ((getRoomId() == null) ? 0 : getRoomId().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getWinner() == null) ? 0 : getWinner().hashCode());
        result = prime * result + ((getLoser() == null) ? 0 : getLoser().hashCode());
        result = prime * result + ((getMoney() == null) ? 0 : getMoney().hashCode());
        result = prime * result + ((getTea() == null) ? 0 : getTea().hashCode());
        result = prime * result + ((getRecordType() == null) ? 0 : getRecordType().hashCode());
        result = prime * result + ((getSettlement() == null) ? 0 : getSettlement().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", roomRecordId=").append(roomRecordId);
        sb.append(", roomId=").append(roomId);
        sb.append(", createTime=").append(createTime);
        sb.append(", winner=").append(winner);
        sb.append(", loser=").append(loser);
        sb.append(", money=").append(money);
        sb.append(", tea=").append(tea);
        sb.append(", recordType=").append(recordType);
        sb.append(", settlement=").append(settlement);
        sb.append("]");
        return sb.toString();
    }
}