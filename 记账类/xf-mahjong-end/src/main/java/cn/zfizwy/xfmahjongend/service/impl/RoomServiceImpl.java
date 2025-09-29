package cn.zfizwy.xfmahjongend.service.impl;

import cn.zfizwy.xfmahjongend.common.Constant;
import cn.zfizwy.xfmahjongend.common.R;
import cn.zfizwy.xfmahjongend.entity.*;
import cn.zfizwy.xfmahjongend.mapper.FriendMapper;
import cn.zfizwy.xfmahjongend.mapper.RoomRecordMapper;
import cn.zfizwy.xfmahjongend.mapper.RoomUserMapper;
import cn.zfizwy.xfmahjongend.service.UserService;
import cn.zfizwy.xfmahjongend.utils.UserContext;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.zfizwy.xfmahjongend.service.RoomService;
import cn.zfizwy.xfmahjongend.mapper.RoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author ChengLexiang
 * @description 针对表【room】的数据库操作Service实现
 * @createDate 2025-09-22 18:00:30
 */
@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room>
        implements RoomService {
    @Autowired
    RoomMapper roomMapper;
    @Autowired
    RoomUserMapper roomUserMapper;
    @Autowired
    RoomRecordMapper roomRecordMapper;
    @Autowired
    FriendMapper friendMapper;

    @Autowired
    UserService userService;

    @Override
    public R createRoom( int mode,BigDecimal  ratio) {
        Room room = new Room();
        room.setCreator(UserContext.get());
        room.setRoomType(mode);
        room.setRoomRatio( ratio);
        int insert = roomMapper.insert(room);
        return new R<>(200, insert > 0 ? "创建成功" : "创建失败", insert > 0 ? room : null);
    }

    @Override
    public R getRoom() {
        LambdaQueryWrapper<RoomUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoomUser::getUserId, UserContext.get());
        queryWrapper.eq(RoomUser::getStatus, 1);
        RoomUser one = roomUserMapper.selectOne(queryWrapper);
        if (one != null) {
            Room room = roomMapper.selectById(one.getRoomId());
            return new R<>(200, "获取成功", room);
        }
        return new R<>(-1, "获取失败", null);
    }

    @Override
    public List<RoomUser> getRoomUser(String roomId) {
        LambdaQueryWrapper<RoomUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoomUser::getRoomId, roomId);

        // 查询房间内所有用户
        List<RoomUser> roomUsers = roomUserMapper.selectList(queryWrapper);
        // 判断是否有好友备注
        for (RoomUser roomUser : roomUsers) {
            if(roomUser.getUserId().equals(UserContext.get())){
                continue;
            }
            LambdaQueryWrapper<Friend> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Friend::getUserId, UserContext.get()).eq(Friend::getToUserId, roomUser.getUserId());
            Friend one = friendMapper.selectOne(queryWrapper1);
            if (one != null&&one.getRemark() != null) {
                roomUser.setNickname(one.getRemark());
            }
        }

        // 将当前用户移到列表首位
        String currentUserId = UserContext.get();
        roomUsers.sort((user1, user2) -> {
            if (user1.getUserId().equals(currentUserId)) {
                return -1;
            } else if (user2.getUserId().equals(currentUserId)) {
                return 1;
            } else {
                return 0;
            }
        });

        return roomUsers;
    }


    @Override
    public byte[] getRoomQrCode(String roomId) {
        //微信生成二维码
        // 获取access_token
        RestTemplate restTemplate = new RestTemplate();
        String tokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + Constant.APP_ID + "&secret=" + Constant.APP_SECRET;
        try {
            String tokenResponse = restTemplate.getForObject(tokenUrl, String.class);
            if (tokenResponse == null) {
                throw new RuntimeException("获取微信access_token失败");
            }

            //fastjson解析
            String accessToken = JSON.parseObject(tokenResponse).getString("access_token");
            System.out.println("accessToken = " + accessToken);
            if (accessToken == null || accessToken.isEmpty()) {
                throw new RuntimeException("解析微信access_token失败: " + tokenResponse);
            }

            // 调用生成不限制小程序码接口
            String qrCodeUrl = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + accessToken;

            // 使用Map构建请求体，避免字符串拼接错误
            java.util.Map<String, Object> requestBodyMap = new java.util.HashMap<>();
            requestBodyMap.put("scene", roomId);
            requestBodyMap.put("env_version", "trial");
            requestBodyMap.put("check_path", false);
            requestBodyMap.put("page", "pages/room");
            String requestBody = JSON.toJSONString(requestBodyMap);

            byte[] result = restTemplate.postForObject(qrCodeUrl, requestBody, byte[].class);
            if (result == null) {
                throw new RuntimeException("生成小程序码失败");
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("生成小程序码异常: " + e.getMessage(), e);
        }
    }

    @Override
    public R checkFriend(String roomId) {
        //获取当前房间的房间用户
        LambdaQueryWrapper<RoomUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoomUser::getRoomId, roomId);
        List<RoomUser> roomUsers = roomUserMapper.selectList(queryWrapper);
        //判断其它用户是否为好友
        for (RoomUser roomUser : roomUsers) {
            if(roomUser.getUserId().equals(UserContext.get())){
                continue;
            }
            LambdaQueryWrapper<Friend> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Friend::getUserId, UserContext.get()).eq(Friend::getToUserId, roomUser.getUserId());
            Friend one = friendMapper.selectOne(queryWrapper1);
            if (one == null) {
                Friend friend = new Friend();
                friend.setUserId(UserContext.get());
                friend.setToUserId(roomUser.getUserId());
                friendMapper.insert(friend);
            }
        }

        return new R<>(200, "检查成功", null);
    }



    @Override
    public R updateTea(Room room) {
        return roomMapper.updateById(room)>0?new R<>(200,"修改成功",null):new R<>(-1,"修改失败",null);
    }

    @Override
    public R updateRemark(String to, String remark) {
        LambdaUpdateWrapper<Friend> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Friend::getUserId, UserContext.get())
                .eq(Friend::getToUserId, to)
                .set(Friend::getRemark, remark);
        int update = friendMapper.update(null, updateWrapper);
        return update > 0 ? new R<>(200, "修改成功", null) : new R<>(-1, "修改失败", null);
    }

    @Override
    public List<RoomRecord> getRoomRecord(String roomId) {
        LambdaQueryWrapper<RoomRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoomRecord::getRoomId, roomId);
        queryWrapper.orderByDesc(RoomRecord::getCreateTime);
        return roomRecordMapper.selectList(queryWrapper);
    }

    @Override
    public R getTea(String roomId) {
        //获取所有用户的Tea
        LambdaQueryWrapper<RoomUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoomUser::getRoomId, roomId);
        List<RoomUser> roomUsers = roomUserMapper.selectList(queryWrapper);
        BigDecimal teaSum = roomUsers.stream().map(RoomUser::getTea).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new R<>(200, "获取成功", teaSum);
    }

    @Override
    public List<RoomRecord> getSettlement(String roomId) {
        LambdaQueryWrapper<RoomRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoomRecord::getRoomId, roomId)
                .eq(RoomRecord::getRecordType, 1)
                .eq(RoomRecord::getSettlement,UserContext.get());

        return roomRecordMapper.selectList(queryWrapper);
    }


}