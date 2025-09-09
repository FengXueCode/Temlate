package cn.zfizwy.killquestionend.controller;

import cn.zfizwy.killquestionend.common.R;
import cn.zfizwy.killquestionend.entity.vo.UserVO;
import cn.zfizwy.killquestionend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping("/init")
    public R<UserVO> weixinLogin(@RequestParam("code") String code) {
        // 通过授权码调用userService进行登录操作
        R<UserVO> res = userService.login(code);
        return res;
    }
}
