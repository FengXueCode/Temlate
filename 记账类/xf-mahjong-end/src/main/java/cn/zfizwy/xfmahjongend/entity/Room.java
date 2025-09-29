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
 * @TableName room
 */
@TableName(value ="room")
@Data
public class Room {
    /**
     * 房间id
     */
    @TableId
    private String roomId;

    /**
     * 创建者id
     */
    private String creator;

    /**
     * 房间状态 0停止 1启用
     */
    private Integer status;

    /**
     * 茶水上限
     */
    private BigDecimal teaLimit;

    /**
     * 0 百分比 1 满额
     */
    private Integer teaType;

    /**
     * 比例
     */
    private Integer ratio;

    /**
     * 满多少
     */
    private BigDecimal teaFull;

    /**
     * 减多少
     */
    private BigDecimal teaMinus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 0 麻将 1 牌
     */
    private Integer roomType;

    /**
     * 分钱比
     */
    private BigDecimal roomRatio;

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
        Room other = (Room) that;
        return (this.getRoomId() == null ? other.getRoomId() == null : this.getRoomId().equals(other.getRoomId()))
            && (this.getCreator() == null ? other.getCreator() == null : this.getCreator().equals(other.getCreator()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getTeaLimit() == null ? other.getTeaLimit() == null : this.getTeaLimit().equals(other.getTeaLimit()))
            && (this.getTeaType() == null ? other.getTeaType() == null : this.getTeaType().equals(other.getTeaType()))
            && (this.getRatio() == null ? other.getRatio() == null : this.getRatio().equals(other.getRatio()))
            && (this.getTeaFull() == null ? other.getTeaFull() == null : this.getTeaFull().equals(other.getTeaFull()))
            && (this.getTeaMinus() == null ? other.getTeaMinus() == null : this.getTeaMinus().equals(other.getTeaMinus()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getRoomType() == null ? other.getRoomType() == null : this.getRoomType().equals(other.getRoomType()))
            && (this.getRoomRatio() == null ? other.getRoomRatio() == null : this.getRoomRatio().equals(other.getRoomRatio()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getRoomId() == null) ? 0 : getRoomId().hashCode());
        result = prime * result + ((getCreator() == null) ? 0 : getCreator().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getTeaLimit() == null) ? 0 : getTeaLimit().hashCode());
        result = prime * result + ((getTeaType() == null) ? 0 : getTeaType().hashCode());
        result = prime * result + ((getRatio() == null) ? 0 : getRatio().hashCode());
        result = prime * result + ((getTeaFull() == null) ? 0 : getTeaFull().hashCode());
        result = prime * result + ((getTeaMinus() == null) ? 0 : getTeaMinus().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getRoomType() == null) ? 0 : getRoomType().hashCode());
        result = prime * result + ((getRoomRatio() == null) ? 0 : getRoomRatio().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", roomId=").append(roomId);
        sb.append(", creator=").append(creator);
        sb.append(", status=").append(status);
        sb.append(", teaLimit=").append(teaLimit);
        sb.append(", teaType=").append(teaType);
        sb.append(", ratio=").append(ratio);
        sb.append(", teaFull=").append(teaFull);
        sb.append(", teaMinus=").append(teaMinus);
        sb.append(", createTime=").append(createTime);
        sb.append(", roomType=").append(roomType);
        sb.append(", roomRatio=").append(roomRatio);
        sb.append("]");
        return sb.toString();
    }
}