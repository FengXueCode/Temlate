package cn.zfizwy.xfmahjongend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.zfizwy.xfmahjongend.entity.Friend;
import cn.zfizwy.xfmahjongend.service.FriendService;
import cn.zfizwy.xfmahjongend.mapper.FriendMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author ChengLexiang
* @description 针对表【friend】的数据库操作Service实现
* @createDate 2025-09-22 18:00:37
*/
@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend>
    implements FriendService{
    @Autowired
    private FriendMapper friendMapper;
    @Override
    public String getFriendRemark(String from, String to) {
        LambdaQueryWrapper<Friend> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Friend::getUserId, from);
        queryWrapper.eq(Friend::getToUserId, to);
        Friend friend = friendMapper.selectOne(queryWrapper);
        if (friend != null) {
            return friend.getRemark();
        }
        return null;
    }
}




