package cn.zfizwy.productservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName product_sku
 */
@TableName(value ="product_sku")
@Data
public class ProductSku {
    /**
     * 商品规格id
     */
    @TableId(type= IdType.ASSIGN_ID)
    private String productSkuId;

    /**
     * 商品id
     */
    private String productId;

    /**
     * 规格属性
     */
    private String attributes;

    /**
     * 规格价格
     */
    private BigDecimal price;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 规格图片
     */
    private String image;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

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
        ProductSku other = (ProductSku) that;
        return (this.getProductSkuId() == null ? other.getProductSkuId() == null : this.getProductSkuId().equals(other.getProductSkuId()))
            && (this.getProductId() == null ? other.getProductId() == null : this.getProductId().equals(other.getProductId()))
            && (this.getAttributes() == null ? other.getAttributes() == null : this.getAttributes().equals(other.getAttributes()))
            && (this.getPrice() == null ? other.getPrice() == null : this.getPrice().equals(other.getPrice()))
            && (this.getStock() == null ? other.getStock() == null : this.getStock().equals(other.getStock()))
            && (this.getImage() == null ? other.getImage() == null : this.getImage().equals(other.getImage()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getProductSkuId() == null) ? 0 : getProductSkuId().hashCode());
        result = prime * result + ((getProductId() == null) ? 0 : getProductId().hashCode());
        result = prime * result + ((getAttributes() == null) ? 0 : getAttributes().hashCode());
        result = prime * result + ((getPrice() == null) ? 0 : getPrice().hashCode());
        result = prime * result + ((getStock() == null) ? 0 : getStock().hashCode());
        result = prime * result + ((getImage() == null) ? 0 : getImage().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", productSkuId=").append(productSkuId);
        sb.append(", productId=").append(productId);
        sb.append(", attributes=").append(attributes);
        sb.append(", price=").append(price);
        sb.append(", stock=").append(stock);
        sb.append(", image=").append(image);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}