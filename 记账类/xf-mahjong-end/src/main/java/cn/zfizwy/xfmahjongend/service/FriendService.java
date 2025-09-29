package cn.zfizwy.xfmahjongend.service;

import cn.zfizwy.xfmahjongend.entity.Friend;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author ChengLexiang
* @description 针对表【friend】的数据库操作Service
* @createDate 2025-09-22 18:00:37
*/
public interface FriendService extends IService<Friend> {
  String getFriendRemark(String from, String to);
}
