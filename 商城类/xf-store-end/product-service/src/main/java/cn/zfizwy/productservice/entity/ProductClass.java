package cn.zfizwy.productservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName product_class
 */
@TableName(value ="product_class")
@Data
public class ProductClass {
    /**
     * 
     */
    @TableId(type= IdType.ASSIGN_ID)
    private String classId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 父分类
     */
    private String parent;

    /**
     * 分类图片
     */
    private String image;

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
        ProductClass other = (ProductClass) that;
        return (this.getClassId() == null ? other.getClassId() == null : this.getClassId().equals(other.getClassId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getParent() == null ? other.getParent() == null : this.getParent().equals(other.getParent()))
            && (this.getImage() == null ? other.getImage() == null : this.getImage().equals(other.getImage()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getClassId() == null) ? 0 : getClassId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getParent() == null) ? 0 : getParent().hashCode());
        result = prime * result + ((getImage() == null) ? 0 : getImage().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", classId=").append(classId);
        sb.append(", name=").append(name);
        sb.append(", parent=").append(parent);
        sb.append(", image=").append(image);
        sb.append("]");
        return sb.toString();
    }
}