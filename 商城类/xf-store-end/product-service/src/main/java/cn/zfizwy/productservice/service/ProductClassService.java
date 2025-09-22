package cn.zfizwy.productservice.service;

import cn.zfizwy.productservice.entity.ProductClass;
import cn.zfizwy.productservice.entity.vo.ProductClassVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author ChengLexiang
* @description 针对表【product_class】的数据库操作Service
* @createDate 2025-09-18 16:05:14
*/
public interface ProductClassService extends IService<ProductClass> {

    List<ProductClassVO> queryAll();

    List<ProductClass> queryTop();

    List<ProductClass> queryChildren(String classId);
}
