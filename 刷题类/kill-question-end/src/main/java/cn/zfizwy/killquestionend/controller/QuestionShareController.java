package cn.zfizwy.killquestionend.controller;


import cn.zfizwy.killquestionend.common.R;
import cn.zfizwy.killquestionend.entity.QuestionShare;
import cn.zfizwy.killquestionend.service.QuestionShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/questionShare")
public class QuestionShareController {
    @Autowired
    private QuestionShareService questionShareService;
    @PostMapping("/add")
    public R addQusetionShare(@RequestBody QuestionShare questionShare){

        return questionShareService.addQusetionShare(questionShare);
    }

}
