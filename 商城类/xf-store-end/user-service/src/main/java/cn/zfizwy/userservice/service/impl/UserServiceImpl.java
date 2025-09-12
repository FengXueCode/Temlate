package cn.zfizwy.userservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.zfizwy.userservice.entity.User;
import cn.zfizwy.userservice.service.UserService;
import cn.zfizwy.userservice.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author ChengLexiang
* @description 针对表【user】的数据库操作Service实现
* @createDate 2025-09-10 14:50:53
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




