package cn.zfizwy.productservice.service.impl;

import cn.zfizwy.commonservice.common.R;
import cn.zfizwy.productservice.entity.Product;
import cn.zfizwy.productservice.mapper.ProductMapper;
import cn.zfizwy.productservice.service.ProductService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ChengLexiang
 * @description 针对表【product】的数据库操作Service实现
 * @createDate 2025-09-19 09:54:48
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
        implements ProductService {
    @Autowired
    private ProductMapper productMapper;

    @Override
    public R updateStatus(String productId, Integer status) {
        LambdaUpdateWrapper<Product> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Product::getProductId, productId);
        updateWrapper.set(Product::getStatus, status);
        int update = productMapper.update(updateWrapper);

        return new R(200,update>0?"更新成功":"更新失败",null);
    }

    @Override
    public List<Product> queryByClass(String classId) {
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getClassId, classId);
       return productMapper.selectList(queryWrapper);
    }
}




