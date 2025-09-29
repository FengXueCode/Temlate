package cn.zfizwy.xfmahjongend.service;

import cn.zfizwy.xfmahjongend.common.R;
import cn.zfizwy.xfmahjongend.entity.User;
import cn.zfizwy.xfmahjongend.entity.vo.UserVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
* @author ChengLexiang
* @description 针对表【user】的数据库操作Service
* @createDate 2025-09-22 15:45:50
*/
public interface UserService extends IService<User> {
    R<UserVO> login(String code);
     R<User> update(String nickName, MultipartFile file);

}
