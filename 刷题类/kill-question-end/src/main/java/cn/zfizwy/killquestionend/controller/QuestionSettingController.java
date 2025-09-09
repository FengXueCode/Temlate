package cn.zfizwy.killquestionend.controller;

import cn.zfizwy.killquestionend.common.R;
import cn.zfizwy.killquestionend.entity.QuestionSetting;
import cn.zfizwy.killquestionend.service.QuestionSettingService;
import cn.zfizwy.killquestionend.utils.UserContext;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/questionSetting")
public class QuestionSettingController {

    @Autowired
    QuestionSettingService questionSettingService;

    @PostMapping("/update")
    public R<Object> update(@RequestBody QuestionSetting questionSetting) {
        questionSetting.setUserId(UserContext.get());
        QueryWrapper<QuestionSetting> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",questionSetting.getUserId());
        boolean exists = questionSettingService.exists(queryWrapper);
        R<Object> r = null;
        if(exists){
            boolean update = questionSettingService.update(questionSetting, queryWrapper);
            if(update){
                r = new R<>(200,"更新成功",null);
            }else{
                r = new R<>(-1,"更新失败",null);
            }
        }else {
            boolean save = questionSettingService.save(questionSetting);
            if(save){
                r = new R<>(200,"更新成功",null);
            }else{
                r = new R<>(-1,"更新失败",null);
            }
        }
        return r;
    }

    @GetMapping("/findByUserId")
    public R<QuestionSetting> findByUserId() {

        QueryWrapper<QuestionSetting> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", UserContext.get());
        QuestionSetting questionSetting = questionSettingService.getOne(queryWrapper);
        if(questionSetting != null){
            return new R<>(200,"查询成功",questionSetting);
        }
        return new R<>(-1,"查询失败",null);
    }
}
