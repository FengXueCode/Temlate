package cn.zfizwy.productservice.entity.vo;

import cn.zfizwy.productservice.entity.ProductClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductClassVO {
    private ProductClass productClass;
    private List<ProductClassVO> children;
}
