package cn.zfizwy.productservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @TableName product
 */
@TableName(value ="product")
@Data
public class Product {
    /**
     * 商品id
     */
    @TableId(type= IdType.ASSIGN_ID)
    private String productId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 主图URL
     */
    private String mainImage;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 销售价格
     */
    private BigDecimal price;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 0 下架 1 上架
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 分类id
     */
    private String classId;

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
        Product other = (Product) that;
        return (this.getProductId() == null ? other.getProductId() == null : this.getProductId().equals(other.getProductId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getMainImage() == null ? other.getMainImage() == null : this.getMainImage().equals(other.getMainImage()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getPrice() == null ? other.getPrice() == null : this.getPrice().equals(other.getPrice()))
            && (this.getStock() == null ? other.getStock() == null : this.getStock().equals(other.getStock()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getClassId() == null ? other.getClassId() == null : this.getClassId().equals(other.getClassId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getProductId() == null) ? 0 : getProductId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getMainImage() == null) ? 0 : getMainImage().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getPrice() == null) ? 0 : getPrice().hashCode());
        result = prime * result + ((getStock() == null) ? 0 : getStock().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getClassId() == null) ? 0 : getClassId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", productId=").append(productId);
        sb.append(", name=").append(name);
        sb.append(", mainImage=").append(mainImage);
        sb.append(", description=").append(description);
        sb.append(", price=").append(price);
        sb.append(", stock=").append(stock);
        sb.append(", status=").append(status);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", classId=").append(classId);
        sb.append("]");
        return sb.toString();
    }
}