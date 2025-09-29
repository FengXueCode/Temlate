package cn.zfizwy.xfmahjongend.controller;


import cn.zfizwy.xfmahjongend.common.R;
import cn.zfizwy.xfmahjongend.entity.User;
import cn.zfizwy.xfmahjongend.entity.vo.UserVO;
import cn.zfizwy.xfmahjongend.service.UserService;
import cn.zfizwy.xfmahjongend.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @GetMapping("/getUser")
    public R<User> getUser() {
        // 调用userService的getUser方法获取当前用户信息
        return new R<>(200, "获取用户信息成功",userService.getById(UserContext.get())) ;
    }

    @PostMapping("/update")
    public R<User> update(@RequestParam(value = "nickName",required = false)String nickName, @RequestParam(value = "file",required = false) MultipartFile file) {
        // 调用userService的update方法更新用户信息
        return userService.update(nickName,file);
    }
}
