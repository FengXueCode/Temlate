package cn.zfizwy.productservice.service.impl;

import cn.zfizwy.productservice.entity.ProductClass;
import cn.zfizwy.productservice.entity.vo.ProductClassVO;
import cn.zfizwy.productservice.mapper.ProductClassMapper;
import cn.zfizwy.productservice.service.ProductClassService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author ChengLexiang
* @description 针对表【product_class】的数据库操作Service实现
* @createDate 2025-09-18 16:05:14
*/
@Service
public class ProductClassServiceImpl extends ServiceImpl<ProductClassMapper, ProductClass>
    implements ProductClassService{
    @Autowired
    private ProductClassMapper productClassMapper;

    @Override
    public List<ProductClassVO> queryAll() {

        LambdaQueryWrapper<ProductClass> queryWrapper = new LambdaQueryWrapper<>();
       queryWrapper.isNull(ProductClass::getParent);
        List<ProductClass> list = productClassMapper.selectList(queryWrapper);
        List<ProductClassVO> list1 = list.stream().map(item -> {
            ProductClassVO productClassVO = new ProductClassVO();
            productClassVO.setProductClass(item);
            productClassVO.setChildren(getChildren(item));
            return productClassVO;
        }).toList();


        return list1;
    }

    @Override
    public List<ProductClass> queryTop() {
        LambdaQueryWrapper<ProductClass> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.isNull(ProductClass::getParent);
        return productClassMapper.selectList(queryWrapper);
    }

    @Override
    public List<ProductClass> queryChildren(String classId) {
        LambdaQueryWrapper<ProductClass> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductClass::getParent, classId);
        return productClassMapper.selectList(queryWrapper);
    }

    private List<ProductClassVO> getChildren(ProductClass productClass){
        LambdaQueryWrapper<ProductClass> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductClass::getParent, productClass.getClassId());
        List<ProductClass> list = productClassMapper.selectList(queryWrapper);
        return list.stream().map(item -> {
            ProductClassVO productClassVO = new ProductClassVO();
            productClassVO.setProductClass(item);
            productClassVO.setChildren(getChildren(item));
            return productClassVO;
        }).toList();
    }
}