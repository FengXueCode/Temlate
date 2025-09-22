package cn.zfizwy.productservice.controller;

import cn.zfizwy.commonservice.common.R;
import cn.zfizwy.productservice.entity.Product;
import cn.zfizwy.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;
    //创建商品
    @PostMapping("/create")
    public R create(@RequestBody Product product) {
        return new R(200,productService.save(product)?"创建成功":"创建失败",null);
    }
    //更改商品
    @PostMapping("/update")
    public R update(@RequestBody Product product) {
        return new R(200,productService.updateById(product)?"更新成功":"更新失败",null);
    }
    //下架or上架商品
    @GetMapping("/updateStatus")
    public R updateStatus(@RequestParam String productId, @RequestParam Integer status) {
        return productService.updateStatus(productId,status);
    }
    //按分类查找商品
    @GetMapping("/queryByClass")
    public List<Product> queryByClass(@RequestParam String classId) {
        return productService.queryByClass(classId);
    }
}
