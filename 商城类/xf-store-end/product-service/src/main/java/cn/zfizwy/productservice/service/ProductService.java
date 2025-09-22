package cn.zfizwy.productservice.service;

import cn.zfizwy.commonservice.common.R;
import cn.zfizwy.productservice.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author ChengLexiang
* @description 针对表【product】的数据库操作Service
* @createDate 2025-09-19 09:54:48
*/
public interface ProductService extends IService<Product> {

    R updateStatus(String productId, Integer status);

    List<Product> queryByClass(String classId);
}
