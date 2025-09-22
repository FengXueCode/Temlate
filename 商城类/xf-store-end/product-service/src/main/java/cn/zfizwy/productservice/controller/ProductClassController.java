package cn.zfizwy.productservice.controller;

import cn.zfizwy.commonservice.common.R;
import cn.zfizwy.productservice.entity.ProductClass;
import cn.zfizwy.productservice.entity.vo.ProductClassVO;
import cn.zfizwy.productservice.service.ProductClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/class")
public class ProductClassController {
    @Autowired
    ProductClassService productClassService;
    //创建分类
    @PostMapping("/create")
    public R create(@RequestBody ProductClass productClass){
        return new R(200,productClassService.save(productClass)?"创建成功":"创建失败",null);
    }
    //删除分类
    @GetMapping("/delete")
    public R delete(@RequestParam String classId){
        return new R(200,productClassService.removeById(classId)?"删除成功":"删除失败",null);
    }
    //修改分类
    @PostMapping("/update")
    public R update(@RequestBody ProductClass productClass){
        return new R(200,productClassService.updateById(productClass)?"修改成功":"修改失败",null);
    }
    //获取顶级分类
    @GetMapping("/queryTop")
    public List<ProductClass> queryTop(){
        return productClassService.queryTop();
    }
    //按id获取子分类
    @GetMapping("/queryChildren")
    public List<ProductClass> queryChildren(@RequestParam String classId){
        return productClassService.queryChildren(classId);
    }
    //查询层级分类
    @GetMapping("/queryAll")
    public List<ProductClassVO> queryAll(){
        return productClassService.queryAll();
    }


}
