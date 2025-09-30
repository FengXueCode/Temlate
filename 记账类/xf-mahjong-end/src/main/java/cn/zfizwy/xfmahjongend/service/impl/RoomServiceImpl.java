package cn.zfizwy.xfmahjongend.service.impl;

import cn.zfizwy.xfmahjongend.common.Constant;
import cn.zfizwy.xfmahjongend.common.R;
import cn.zfizwy.xfmahjongend.entity.*;
import cn.zfizwy.xfmahjongend.entity.vo.SettlementVO;
import cn.zfizwy.xfmahjongend.mapper.FriendMapper;
import cn.zfizwy.xfmahjongend.mapper.RoomRecordMapper;
import cn.zfizwy.xfmahjongend.mapper.RoomUserMapper;
import cn.zfizwy.xfmahjongend.service.*;
import cn.zfizwy.xfmahjongend.utils.UserContext;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.zfizwy.xfmahjongend.mapper.RoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    @Autowired
    RoomUserService roomUserService;
    @Autowired
    RoomRecordService roomRecordService;
    @Autowired
    RoomEndingService roomEndingService;


    @Override
    public R createRoom(int mode, BigDecimal ratio) {
        Room room = new Room();
        room.setCreator(UserContext.get());
        room.setRoomType(mode);
        room.setRoomRatio(ratio);
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
            if (roomUser.getUserId().equals(UserContext.get())) {
                continue;
            }
            LambdaQueryWrapper<Friend> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Friend::getUserId, UserContext.get()).eq(Friend::getToUserId, roomUser.getUserId());
            Friend one = friendMapper.selectOne(queryWrapper1);
            if (one != null && one.getRemark() != null) {
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
            if (roomUser.getUserId().equals(UserContext.get())) {
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
        return roomMapper.updateById(room) > 0 ? new R<>(200, "修改成功", null) : new R<>(-1, "修改失败", null);
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
        // 先查询出满足条件的最新创建时间
        LambdaQueryWrapper<RoomRecord> maxTimeQuery = new LambdaQueryWrapper<>();
        maxTimeQuery.eq(RoomRecord::getRoomId, roomId)
                .eq(RoomRecord::getRecordType, 1)
                .and(wrapper -> wrapper
                        .eq(RoomRecord::getLoser, UserContext.get())
                        .or()
                        .eq(RoomRecord::getWinner, UserContext.get())
                );
        maxTimeQuery.orderByDesc(RoomRecord::getCreateTime);
        maxTimeQuery.last("LIMIT 1");

        RoomRecord latestRecord = roomRecordMapper.selectOne(maxTimeQuery);

        if (latestRecord == null) {
            return List.of(); // 如果没有找到记录，返回空列表
        }

        // 再查询创建时间等于最新时间的所有记录
        LambdaQueryWrapper<RoomRecord> settlementRecordQuery = new LambdaQueryWrapper<>();
        settlementRecordQuery.eq(RoomRecord::getRoomId, roomId)
                .eq(RoomRecord::getRecordType, 1)
                .eq(RoomRecord::getCreateTime, latestRecord.getCreateTime())
                .and(wrapper -> wrapper
                        .eq(RoomRecord::getLoser, UserContext.get())
                        .or()
                        .eq(RoomRecord::getWinner, UserContext.get())
                );

        return roomRecordMapper.selectList(settlementRecordQuery);
    }

    @Override
    public void settlement(String roomId, String userId, Boolean flag) {
        //获取当前用户
        LambdaQueryWrapper<RoomUser> queryUserWrapper = new LambdaQueryWrapper<>();
        queryUserWrapper.eq(RoomUser::getRoomId, roomId).eq(RoomUser::getUserId, userId);
        RoomUser user = roomUserService.getOne(queryUserWrapper);

        // 获取房间所有用户
        LambdaQueryWrapper<RoomUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoomUser::getRoomId, roomId);
        queryWrapper.eq(RoomUser::getStatus, 1);
        List<RoomUser> roomUsers = roomUserMapper.selectList(queryWrapper);
        for(RoomUser roomUser : roomUsers){
           if(roomUser.getUserId().equals(userId)){
               continue;
           }
            //获取俩人之间是否有结算记录
            LambdaQueryWrapper<RoomRecord> settlementRecordQuery = new LambdaQueryWrapper<>();
            settlementRecordQuery.eq(RoomRecord::getRoomId, roomId)
                    .eq(RoomRecord::getRecordType, 1)
                    .and(wrapper -> wrapper.eq(RoomRecord::getWinner, roomUser.getUserId())
                            .eq(RoomRecord::getLoser, userId)
                            .or()
                            .eq(RoomRecord::getWinner, userId)
                            .eq(RoomRecord::getLoser, roomUser.getUserId())
                    );
            RoomRecord one = roomRecordMapper.selectOne(settlementRecordQuery);
            //获取俩人之间的交易记录，如果存在结算记录，则只获取结算后的新记录
            LambdaQueryWrapper<RoomRecord> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(RoomRecord::getRoomId, roomId)
                    .eq(RoomRecord::getRecordType, 0)
                    .and(wrapper -> wrapper.eq(RoomRecord::getWinner, roomUser.getUserId())
                            .eq(RoomRecord::getLoser, userId)
                            .or()
                            .eq(RoomRecord::getWinner, userId)
                            .eq(RoomRecord::getLoser, roomUser.getUserId())
                    );
            if (one != null) {
                queryWrapper2.ge(RoomRecord::getCreateTime, one.getCreateTime());
            }
            List<RoomRecord> records = roomRecordMapper.selectList(queryWrapper2);
            //计算交易结余(累加winner为userId金额减去loser为userId金额)
            BigDecimal balance = records.stream().map(record -> {
                if (record.getWinner().equals(userId)) {
                    return record.getMoney();
                } else {
                    return record.getMoney().negate();
                }
            }).reduce(BigDecimal.ZERO, BigDecimal::add);

            //添加结算记录
            RoomRecord roomRecord = new RoomRecord();
            roomRecord.setRoomId(roomId);
            roomRecord.setRecordType(1);
            roomRecord.setSettlement(user.getUserId());
            //判断结余是否大于0
            if (balance.compareTo(BigDecimal.ZERO) > 0) {
                roomRecord.setWinner(userId);
                roomRecord.setLoser(roomUser.getUserId());
            } else {
                roomRecord.setWinner(roomUser.getUserId());
                roomRecord.setLoser(userId);
            }
            roomRecord.setMoney(balance);
            roomRecord.setTea(user.getTea());

            roomRecordService.save(roomRecord);
            //更新用户金额
            if(flag){
                roomUser.setMoney(roomUser.getMoney().add(balance));
                user.setStatus(0);
                roomUserService.update(roomUser, new LambdaQueryWrapper<RoomUser>().eq(RoomUser::getRoomId, roomId).eq(RoomUser::getUserId, roomUser.getUserId()));
                roomUserService.update(user, new LambdaQueryWrapper<RoomUser>().eq(RoomUser::getRoomId, roomId).eq(RoomUser::getUserId, userId));

            }

        }
        // 记录场次胜负
        RoomEnding roomEnding = new RoomEnding();
        roomEnding.setRoomId(roomId);
        roomEnding.setUserId(userId);
        roomEnding.setLocation(user.getLocation());
        roomEnding.setMoney(user.getMoney());
        roomEndingService.save(roomEnding);

    }

    @Override
    public void roomSettlement(String roomId) {

        LambdaQueryWrapper<RoomUser> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(RoomUser::getRoomId, roomId).eq(RoomUser::getStatus, 1);

        // 1.获取所有在线用户
        List<RoomUser> roomUsers = roomUserService.list(userQueryWrapper);
        // 2. 遍历用户 与 其它用户结算
        for (RoomUser roomUser : roomUsers) {
            settlement(roomId, roomUser.getUserId(), false);
            // 3. 修改用户状态
            LambdaUpdateWrapper<RoomUser> roomUserUpdateWrapper = new LambdaUpdateWrapper<>();
            roomUserUpdateWrapper
                    .eq(RoomUser::getRoomId, roomId)
                    .eq(RoomUser::getUserId, roomUser.getUserId())
                    .set(RoomUser::getStatus, 0)
            ;

            roomUserService.update(roomUserUpdateWrapper);
        }
        // 4. 修改房间状态
        LambdaUpdateWrapper<Room> roomUpdateWrapper = new LambdaUpdateWrapper<>();
        roomUpdateWrapper.eq(Room::getRoomId, roomId).set(Room::getStatus, 0);
        roomMapper.update(roomUpdateWrapper);


    }

@Override
public List<RoomEnding> getRoomEnding(String roomId) {
    // 使用子查询获取每个用户的最新记录
    LambdaQueryWrapper<RoomEnding> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(RoomEnding::getRoomId, roomId);

    // 子查询：获取每个userId的最大(createTime)
    queryWrapper.apply("EXISTS (SELECT 1 FROM room_ending re2 WHERE re2.user_id = room_ending.user_id AND re2.room_id = {0} GROUP BY re2.user_id HAVING MAX(re2.create_time) = room_ending.create_time)", roomId);

    queryWrapper.orderByDesc(RoomEnding::getMoney);

    return roomEndingService.list(queryWrapper);
}



}