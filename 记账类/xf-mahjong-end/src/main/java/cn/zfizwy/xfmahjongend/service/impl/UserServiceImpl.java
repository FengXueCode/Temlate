package cn.zfizwy.xfmahjongend.service.impl;


import cn.zfizwy.xfmahjongend.common.R;
import cn.zfizwy.xfmahjongend.entity.User;
import cn.zfizwy.xfmahjongend.entity.vo.UserVO;
import cn.zfizwy.xfmahjongend.mapper.UserMapper;
import cn.zfizwy.xfmahjongend.service.UserService;
import cn.zfizwy.xfmahjongend.utils.FileUtil;
import cn.zfizwy.xfmahjongend.utils.TokenUtil;
import cn.zfizwy.xfmahjongend.utils.UserContext;
import cn.zfizwy.xfmahjongend.utils.WeChatUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * @author ChengLexiang
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2025-09-08 10:19:22
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

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
            userVO.setNickName(exists.getNickname());
            userVO.setPortrait(exists.getPortrait());
            userVO.setToken(TokenUtil.sign(exists.getUserId()));
//            redisTemplate.opsForValue().set(token, exists.getUserId());
            return new R<>(200, "登录成功", userVO);
        }


        //创建用户后登录
        User user = new User();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String secureRandomStr = generateSecureRandomString(6, chars);
        user.setNickname("用户" + secureRandomStr);
        user.setOpenId(openId);
        userMapper.insert(user);
        userVO.setUserId(user.getUserId());
        userVO.setNickName(user.getNickname());
        userVO.setPortrait(user.getPortrait());
        userVO.setToken(TokenUtil.sign(user.getUserId()));

        return new R<>(200, "登录成功", userVO);
    }

    @Override
    public R<User> update(String nickName, MultipartFile file) {


        QueryWrapper<User> wrapper = new QueryWrapper();
        wrapper.eq("user_id", UserContext.get());
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            return new R<>(-1, "用户不存在", null);
        }
        user.setNickname(nickName);

        if (file != null) {
            String path = FileUtil.savePortrait(file);
            System.out.println(path);
            if (path.equals("上传失败")) {
                return new R<>(-1, "头像上传失败", null);
            }
            user.setPortrait(path);

        }

        int update = userMapper.update(user, wrapper);
        if (update == 0) {
            return new R<>(-1, "更新失败", null);
        }
        return new R<>(200, "更新成功", user);

    }
}




