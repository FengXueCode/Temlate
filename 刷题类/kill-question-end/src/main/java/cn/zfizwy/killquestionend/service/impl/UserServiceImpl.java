package cn.zfizwy.killquestionend.service.impl;

import cn.zfizwy.killquestionend.common.R;
import cn.zfizwy.killquestionend.entity.vo.UserVO;
import cn.zfizwy.killquestionend.utils.TokenUtil;
import cn.zfizwy.killquestionend.utils.WeChatUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.zfizwy.killquestionend.entity.User;
import cn.zfizwy.killquestionend.service.UserService;
import cn.zfizwy.killquestionend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

/**
* @author ChengLexiang
* @description 针对表【user】的数据库操作Service实现
* @createDate 2025-09-08 10:19:22
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Autowired
    private UserMapper userMapper;

    @Qualifier("redisTemplate")

    public static String generateSecureRandomString(int length, String allowedChars) {
        StringBuilder stringBuilder = new StringBuilder(length);
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(allowedChars.length());
            char randomChar = allowedChars.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }
    @Override
    public R<UserVO> login(String code) {
        //获取openId
        String openId = WeChatUtil.getOpenID(code);
        openId = new StringBuilder(openId).reverse().toString();
        openId = Base64.getEncoder().encodeToString(openId.getBytes());
        UserVO userVO = new UserVO();


//      检查数据库中是否存在该用户
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("open_id", openId);
        User exists = userMapper.selectOne(wrapper);

        if (exists != null) {
            userVO.setUserId(exists.getUserId());
            userVO.setNickName(exists.getNickName());
            userVO.setPortrait(exists.getPortrait());
            userVO.setToken(TokenUtil.sign(exists.getUserId()));
//            redisTemplate.opsForValue().set(token, exists.getUserId());
            return new R<>(200, "登录成功", userVO);
        }



        //创建用户后登录
        User user = new User();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String secureRandomStr = generateSecureRandomString(10, chars);
        user.setNickName("用户" + secureRandomStr);
        user.setOpenId(openId);
        userMapper.insert(user);
        userVO.setUserId(user.getUserId());
        userVO.setNickName(user.getNickName());
        userVO.setPortrait(user.getPortrait());
        userVO.setToken(TokenUtil.sign(user.getUserId()));

        return new R<>(200, "登录成功", userVO);
    }
}




