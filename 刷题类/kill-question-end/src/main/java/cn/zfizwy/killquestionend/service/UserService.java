package cn.zfizwy.killquestionend.service;

import cn.zfizwy.killquestionend.common.R;
import cn.zfizwy.killquestionend.entity.User;
import cn.zfizwy.killquestionend.entity.vo.UserVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author ChengLexiang
* @description 针对表【user】的数据库操作Service
* @createDate 2025-09-08 10:19:22
*/
public interface UserService extends IService<User> {

    R<UserVO> login(String code);
}
