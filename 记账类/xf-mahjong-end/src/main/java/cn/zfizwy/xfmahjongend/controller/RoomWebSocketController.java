package cn.zfizwy.xfmahjongend.controller;

import cn.zfizwy.xfmahjongend.entity.*;
import cn.zfizwy.xfmahjongend.entity.vo.SeatVO;
import cn.zfizwy.xfmahjongend.entity.vo.TransferVO;
import cn.zfizwy.xfmahjongend.service.*;
import cn.zfizwy.xfmahjongend.utils.TokenUtil;
import cn.zfizwy.xfmahjongend.utils.UserContext;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@CrossOrigin
@Component
@ServerEndpoint("/websocket/{roomId}/{userId}")
public class RoomWebSocketController {
    private static ApplicationContext applicationContext;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RoomWebSocketController.class);

    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    private static Map<String, Map<String, Session>> roomSessions = new ConcurrentHashMap<>();
    private static Map<String, String> sessionRoomMap = new ConcurrentHashMap<>();
    private String roomId;
    private Session session;


    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") String roomId, @PathParam("userId") String userId) {
        // 通过ApplicationContext获取Service实例
        UserService userService = applicationContext.getBean(UserService.class);
        RoomUserService roomUserService = applicationContext.getBean(RoomUserService.class);
        RoomService roomService = applicationContext.getBean(RoomService.class);


        this.roomId = roomId;
        this.session = session;

        roomSessions.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(session.getId(), session);
        sessionRoomMap.put(session.getId(), roomId);


        User user = userService.getById(userId);
        LambdaQueryWrapper<RoomUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoomUser::getRoomId, roomId);
        queryWrapper.eq(RoomUser::getUserId, user.getUserId());
        RoomUser one = roomUserService.getOne(queryWrapper);
        //获取房间
        Room room = roomService.getById(roomId);

        //判断是否达到人数上线
        LambdaQueryWrapper<RoomUser> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(RoomUser::getRoomId, roomId).eq(RoomUser::getStatus, 1);
        List<RoomUser> list = roomUserService.list(queryWrapper1);


        if (one == null) {
            if (list.size() == (room.getRoomType() == 0 ? 4 : 8)) {
                sendMsg(session, "房间已满");
                return;
            }
            RoomUser roomUser = new RoomUser();
            roomUser.setRoomId(roomId);
            roomUser.setUserId(userId);
            roomUser.setNickname(user.getNickname());
            roomUser.setPortrait(user.getPortrait());
            //判断座位
            if (room.getRoomType() == 0) {
                int location = findAvailableSeat(roomUserService, roomId);
                roomUser.setLocation(location);
            }

            roomUserService.save(roomUser);
            broadcastToAllInRoom(roomId, user.getNickname() + "加入房间");

        } else {
            if (one.getStatus() == 0) {
                one.setMoney(BigDecimal.ZERO);
                broadcastToAllInRoom(roomId, user.getNickname() + "加入房间");

            }
            one.setStatus(1);
            roomUserService.update(one, queryWrapper);
        }

        System.out.println("WebSocket 连接已开启，Session ID: " + session.getId() + "， ID: " + roomId);
        broadcastToAllInRoom(roomId, "刷新用户数据");


    }

    @OnClose
    public void onClose(Session session) {
        String roomId = sessionRoomMap.remove(session.getId());
        if (roomId != null) {
            Map<String, Session> sessions = roomSessions.get(roomId);
            if (sessions != null) {
                Session removedSession = sessions.remove(session.getId());
                if (removedSession != null) {
                    System.out.println("WebSocket 连接已关闭，Session ID: " + session.getId() + "， ID: " + roomId);
                }

                // 如果该 roomId 下没有 session 了，清理这个 map
                if (sessions.isEmpty()) {
                    roomSessions.remove(roomId);
                }
            }
        } else {
            System.out.println("未找到要关闭的 Session，ID: " + session.getId());
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        String roomId = sessionRoomMap.get(session.getId());
        System.out.println("message = " + message);

        //转账功能
        if (message.contains("转账")) {
            transfer(roomId, message);
        }
        //个人结算
        if (message.contains("个人结算")) {
            String userId = message.split("=")[1];
            if (StringUtils.isBlank(roomId) || StringUtils.isBlank(message)) {
                throw new IllegalArgumentException("roomId or message cannot be blank");
            }

            int equalsIndex = message.indexOf('=');
            if (equalsIndex == -1 || equalsIndex == message.length() - 1) {
                throw new IllegalArgumentException("Invalid message format: missing '=' or userId");
            }
            settlement(roomId, userId, true);
        }
        //交互座位
        if (message.contains("切换座位")) {
            changeSeat(roomId, message);
        }
        //房间结算
        if (message.contains("房间结算")) {
            roomSettlement(roomId);
        }

    }

    //切换座位
    private void changeSeat(String roomId, String message) {

        //将message使用fastjson转换为解析为TransferVO对象
        SeatVO seatVO = JSON.parseObject(message.split("=")[1], SeatVO.class);
        System.out.println("seatVO = " + seatVO);
        RoomUserService roomUserService = applicationContext.getBean(RoomUserService.class);

        if (seatVO.getToUserId().isEmpty()) {
            LambdaUpdateWrapper<RoomUser> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(RoomUser::getRoomId, roomId)
                    .eq(RoomUser::getUserId, seatVO.getUserId())
                    .set(RoomUser::getLocation, seatVO.getNewLocation());
            roomUserService.update(null, updateWrapper);
        } else {
            //获取俩边玩家信息
            LambdaQueryWrapper<RoomUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RoomUser::getRoomId, roomId)
                    .eq(RoomUser::getUserId, seatVO.getUserId());
            RoomUser one = roomUserService.getOne(queryWrapper);
            LambdaQueryWrapper<RoomUser> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(RoomUser::getRoomId, roomId)
                    .eq(RoomUser::getUserId, seatVO.getToUserId());
            RoomUser two = roomUserService.getOne(queryWrapper1);
            //交换俩个玩家的location
            Integer oneLocation = one.getLocation();
            one.setLocation(two.getLocation());
            two.setLocation(oneLocation);
            roomUserService.update(one, queryWrapper);
            roomUserService.update(two, queryWrapper1);
        }
        broadcastToAllInRoom(roomId, "刷新用户数据");

    }

    //个人结算
    protected void settlement(String roomId, String userId, Boolean flag) {
        RoomUserService roomUserService = applicationContext.getBean(RoomUserService.class);
        RoomRecordService roomRecordService = applicationContext.getBean(RoomRecordService.class);
        RoomEndingService roomEndingService = applicationContext.getBean(RoomEndingService.class);
        //获取当前用户
        LambdaQueryWrapper<RoomUser> queryUserWrapper = new LambdaQueryWrapper<>();
        queryUserWrapper.eq(RoomUser::getRoomId, roomId).eq(RoomUser::getUserId, userId);
        RoomUser user = roomUserService.getOne(queryUserWrapper);
        // 记录场次胜负
        RoomEnding roomEnding = new RoomEnding();
        roomEnding.setRoomId(roomId);
        roomEnding.setUserId(userId);
        roomEnding.setLocation(user.getLocation());
        roomEnding.setMoney(user.getMoney());
        roomEndingService.save(roomEnding);
        // 获取房间所有用户
        LambdaQueryWrapper<RoomUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoomUser::getRoomId, roomId);
        queryWrapper.eq(RoomUser::getStatus, 1);
        List<RoomUser> roomUsers = roomUserService.list(queryWrapper);

        for (RoomUser roomUser : roomUsers) {
            if (roomUser.getUserId().equals(userId)) {
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
            RoomRecord one = roomRecordService.getOne(settlementRecordQuery);
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
            List<RoomRecord> records = roomRecordService.list(queryWrapper2);
            //计算交易结余(累加winner为userId金额减去loser为userId金额)
            BigDecimal balance = records.stream().map(record -> {
                if (record.getWinner().equals(userId)) {
                    return record.getMoney();
                } else {
                    return record.getMoney().negate();
                }
            }).reduce(BigDecimal.ZERO, BigDecimal::add);

            //如果结余为0，则不记录
            if (balance.compareTo(BigDecimal.ZERO) == 0) {
              continue;
            }

            //添加结算记录
            RoomRecord roomRecord = new RoomRecord();
            roomRecord.setRoomId(roomId);
            roomRecord.setRecordType(1);
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
            roomRecord.setSettlement(userId);
            roomRecordService.save(roomRecord);
            //更新用户金额
            if (flag) {
                roomUser.setMoney(roomUser.getMoney().add(balance));
                user.setMoney(user.getMoney().subtract(balance));
                user.setStatus(0);
                roomUserService.update(roomUser, new LambdaQueryWrapper<RoomUser>().eq(RoomUser::getRoomId, roomId).eq(RoomUser::getUserId, roomUser.getUserId()));
                roomUserService.update(user, new LambdaQueryWrapper<RoomUser>().eq(RoomUser::getRoomId, roomId).eq(RoomUser::getUserId, userId));
                roomRecord = roomRecordService.getById(roomRecord.getRoomRecordId());
                broadcastToAllInRoom(roomId, "个人结算="+JSON.toJSONString(roomRecord));
                broadcastToAllInRoom(roomId, "刷新用户数据");


            }

        }

    }

    //房间结算
    protected void roomSettlement(String roomId) {

        RoomUserService roomUserService = applicationContext.getBean(RoomUserService.class);
        RoomRecordService roomRecordService = applicationContext.getBean(RoomRecordService.class);
        RoomEndingService roomEndingService = applicationContext.getBean(RoomEndingService.class);

        LambdaQueryWrapper<RoomUser> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(RoomUser::getRoomId, roomId).eq(RoomUser::getStatus, 1);

        // 1.获取所有在线用户
        List<RoomUser> roomUsers = roomUserService.list(userQueryWrapper);
        // 2.记录房间用户胜负信息
        roomUsers.forEach(roomUser -> {
            // 记录场次胜负
            RoomEnding roomEnding = new RoomEnding();
            roomEnding.setRoomId(roomId);
            roomEnding.setUserId(roomUser.getUserId());
            roomEnding.setLocation(roomUser.getLocation());
            roomEnding.setMoney(roomUser.getMoney());
            roomEndingService.save(roomEnding);
        });
        
        // 3. 按金额排序，负数给正数转账
        // 金额最低的优先给金额最高的转账
        List<RoomUser> losers = roomUsers.stream()
                .filter(user -> user.getMoney().compareTo(BigDecimal.ZERO) < 0)
                .sorted((u1, u2) -> u1.getMoney().compareTo(u2.getMoney())) // 从小到大排序（负数）
                .collect(Collectors.toList());

        List<RoomUser> winners = roomUsers.stream()
                .filter(user -> user.getMoney().compareTo(BigDecimal.ZERO) > 0)
                .sorted((u1, u2) -> u2.getMoney().compareTo(u1.getMoney())) // 从大到小排序
                .collect(Collectors.toList());

        // 4. 进行债务抵扣
        for (RoomUser loser : losers) {
            BigDecimal debt = loser.getMoney().abs(); // 负债金额
            
            for (RoomUser winner : winners) {
                if (debt.compareTo(BigDecimal.ZERO) <= 0) {
                    break; // 债务已还清
                }
                
                BigDecimal credit = winner.getMoney(); // 债权金额
                
                if (credit.compareTo(BigDecimal.ZERO) <= 0) {
                    continue; // 跳过已经没有债权的赢家
                }
                
                // 计算实际可抵扣金额
                BigDecimal transferAmount = debt.min(credit);
                
                // 添加结算记录
                RoomRecord roomRecord = new RoomRecord();
                roomRecord.setRoomId(roomId);
                roomRecord.setRecordType(1);
                roomRecord.setWinner(winner.getUserId());
                roomRecord.setLoser(loser.getUserId());
                roomRecord.setMoney(transferAmount);
                roomRecord.setTea(loser.getTea());
                roomRecord.setSettlement(loser.getUserId()); // 房间结算标识
                roomRecordService.save(roomRecord);
                
                // 更新债务和债权
                debt = debt.subtract(transferAmount);
                winner.setMoney(credit.subtract(transferAmount));
                
//                // 广播结算记录
//                broadcastToAllInRoom(roomId, "个人结算="+JSON.toJSONString(roomRecord));
            }
        }
        
        // 5. 修改用户状态
        for (RoomUser roomUser : roomUsers) {
            LambdaUpdateWrapper<RoomUser> roomUserUpdateWrapper = new LambdaUpdateWrapper<>();
            roomUserUpdateWrapper
                    .eq(RoomUser::getRoomId, roomId)
                    .eq(RoomUser::getUserId, roomUser.getUserId())
                    .set(RoomUser::getStatus, 0)
            ;

            roomUserService.update(roomUserUpdateWrapper);
        }
        
        // 6. 修改房间状态
        RoomService roomService = applicationContext.getBean(RoomService.class);
        LambdaUpdateWrapper<Room> roomUpdateWrapper = new LambdaUpdateWrapper<>();
        roomUpdateWrapper.eq(Room::getRoomId, roomId).set(Room::getStatus, 0);
        roomService.update(roomUpdateWrapper);
        
        broadcastToAllInRoom(roomId, "房间结算");

    }

    //转账功能
    private void transfer(String roomId, String message) {
        // 1. 消息格式校验
        String[] split = message.split(":", 2);
        if (split.length < 2) {
            log.warn("Invalid message format: {}", message);
            return;
        }

        // 2. JSON 解析
        TransferVO transferVO;
        try {
            transferVO = JSON.parseObject(split[1], TransferVO.class);
        } catch (Exception e) {
            log.error("Failed to parse TransferVO from message: {}", message, e);
            return;
        }

        // 3. 参数校验
        if (transferVO == null || transferVO.getFrom() == null || transferVO.getTo() == null || transferVO.getAmount() == null) {
            log.warn("Invalid TransferVO: {}", transferVO);
            return;
        }

        // 4. 获取服务（建议改为 @Autowired）
        RoomService roomService = applicationContext.getBean(RoomService.class);
        RoomUserService roomUserService = applicationContext.getBean(RoomUserService.class);
        RoomRecordService roomRecordService = applicationContext.getBean(RoomRecordService.class);
        FriendService friendService = applicationContext.getBean(FriendService.class);

        // 5. 查询用户信息
        LambdaQueryWrapper<RoomUser> queryWrapperFrom = new LambdaQueryWrapper<>();
        queryWrapperFrom.eq(RoomUser::getRoomId, roomId).eq(RoomUser::getUserId, transferVO.getFrom());
        RoomUser user = roomUserService.getOne(queryWrapperFrom);

        LambdaQueryWrapper<RoomUser> queryWrapperTo = new LambdaQueryWrapper<>();
        queryWrapperTo.eq(RoomUser::getRoomId, roomId).eq(RoomUser::getUserId, transferVO.getTo());
        RoomUser transferUser = roomUserService.getOne(queryWrapperTo);

        if (user == null || transferUser == null) {
            log.warn("User not found in room. From: {}, To: {}", transferVO.getFrom(), transferVO.getTo());
            return;
        }

        // 6. 构造转账记录
        RoomRecord roomRecord = new RoomRecord();
        roomRecord.setRoomId(roomId);
        roomRecord.setWinner(transferUser.getUserId());
        roomRecord.setLoser(user.getUserId());
        roomRecord.setMoney(transferVO.getAmount());
        roomRecord.setRecordType(0);

        // 7. 更新用户余额
        user.setMoney(user.getMoney().subtract(transferVO.getAmount()));

        // 8. 获取房间信息
        Room room = roomService.getById(roomId);
        if (room == null) {
            log.warn("Room not found: {}", roomId);
            return;
        }

        // 9. 计算茶水费总额
        LambdaQueryWrapper<RoomUser> queryWrapperAll = new LambdaQueryWrapper<>();
        queryWrapperAll.eq(RoomUser::getRoomId, roomId);
        List<RoomUser> roomUsers = roomUserService.list(queryWrapperAll);
        BigDecimal teaSum = roomUsers.stream()
                .map(RoomUser::getTea)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 10. 处理转账与茶水费
        processTransferWithTeaFee(transferVO, transferUser, room, teaSum);

        // 11. 更新数据库（建议使用事务）
        boolean updateFrom = roomUserService.update(user, queryWrapperFrom);
        boolean updateTo = roomUserService.update(transferUser, queryWrapperTo);
        boolean recordSaved = roomRecordService.save(roomRecord);

        if (updateFrom && updateTo && recordSaved) {
            roomRecord = roomRecordService.getById(roomRecord.getRoomRecordId());
            broadcastTransferResult(roomId, roomRecord);
        } else {
            log.error("Failed to complete transfer. From update: {}, To update: {}, Record saved: {}", updateFrom, updateTo, recordSaved);
        }
    }

    // 获取显示名称
    private String getDisplayName(FriendService friendService, String userId, String targetId, String defaultName) {
        String remark = friendService.getFriendRemark(userId, targetId);
        return (remark != null && !remark.isEmpty()) ? remark : defaultName;
    }

    // 广播转账结果
    private void broadcastTransferResult(String roomId, RoomRecord roomRecord) {
        broadcastToAllInRoom(roomId, "刷新用户数据");
        broadcastToAllInRoom(roomId, "转账记录=" + JSON.toJSONString(roomRecord));
    }


    /**
     * 处理带茶水费的转账逻辑
     *
     * @param transferVO   转账信息
     * @param transferUser 收款用户
     * @param room         房间信息
     * @param teaSum       当前茶水费总额
     */
    private void processTransferWithTeaFee(TransferVO transferVO, RoomUser transferUser, Room room, BigDecimal teaSum) {
        // 如果茶水费限制为0，表示不设上限，直接转账
        if (room.getTeaLimit().compareTo(BigDecimal.ZERO) == 0) {
            transferUser.setMoney(transferUser.getMoney().add(transferVO.getAmount()));
            return;
        }

        // 如果茶水费已达上限，则直接转账
        if (teaSum.compareTo(room.getTeaLimit()) >= 0) {
            transferUser.setMoney(transferUser.getMoney().add(transferVO.getAmount()));
            return;
        }

        BigDecimal tea = BigDecimal.ZERO;
        boolean shouldChargeTea = false;

        // 根据茶水类型计算茶水费
        switch (room.getTeaType()) {
            case 1: // 按比例收费
                tea = transferVO.getAmount().multiply(new BigDecimal(room.getRatio() * 0.01)).setScale(2, BigDecimal.ROUND_HALF_UP);
                shouldChargeTea = true;
                break;
            case 2: // 满减收费
                if (transferVO.getAmount().compareTo(room.getTeaFull()) >= 0) {
                    tea = room.getTeaMinus(); // 收取固定茶水费
                    shouldChargeTea = true;
                }
                break;
            default:
                // 其他情况不收茶水费
                break;
        }

        // 如果需要收取茶水费，则进行相应计算
        if (shouldChargeTea) {
            // 检查是否会超出茶水限制
            BigDecimal newTeaSum = teaSum.add(tea);
            if (newTeaSum.compareTo(room.getTeaLimit()) > 0) {
                // 如果超出限制，只收取到限制额度的茶水费
                BigDecimal actualTea = room.getTeaLimit().subtract(teaSum);
                transferUser.setTea(transferUser.getTea().add(actualTea));
                transferUser.setMoney(transferUser.getMoney().add(transferVO.getAmount().subtract(actualTea)));
            } else {
                transferUser.setTea(transferUser.getTea().add(tea));
                transferUser.setMoney(transferUser.getMoney().add(transferVO.getAmount().subtract(tea)));
            }
        } else {
            // 不收取茶水费，全额转账
            transferUser.setMoney(transferUser.getMoney().add(transferVO.getAmount()));
        }
    }

    private synchronized void sendMsg(Session session, String msg) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendText(msg);
            } else {
                // 如果 session 已关闭，清理相关记录
                cleanClosedSession(session);
            }
        } catch (IOException e) {
            System.err.println("发送消息失败: " + e.getMessage());
            // 发生异常时也清理 session
            cleanClosedSession(session);
        }
    }

    private void cleanClosedSession(Session session) {
        String roomId = sessionRoomMap.remove(session.getId());
        if (roomId != null) {
            Map<String, Session> sessions = roomSessions.get(roomId);
            if (sessions != null) {
                sessions.remove(session.getId());
                if (sessions.isEmpty()) {
                    roomSessions.remove(roomId);
                }
            }
        }
    }

    // 向同一 roomId 的所有 session 广播消息（排除发送者）
    private void broadcastToRoom(String roomId, String message, String senderSessionId) {
        Map<String, Session> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            for (Session session : sessions.values()) {
                // 排除发送者自己
                if (!session.getId().equals(senderSessionId) && session.isOpen()) {
                    try {
                        session.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        System.err.println("广播消息失败: " + e.getMessage());
                        // 发送失败时清理 session
                        cleanClosedSession(session);
                    }
                }
            }
        }
    }

    // 向同一 roomId 的所有 session 广播消息（包括发送者）
    public static void broadcastToAllInRoom(String roomId, String message) {
        Map<String, Session> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            for (Session session : sessions.values()) {
                if (session.isOpen()) {
                    try {
                        session.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        System.err.println("广播消息失败: " + e.getMessage());
                    }
                }
            }
        }
    }

    // 提供获取指定 roomId 的在线人数的方法
    public static int getOnlineCount(String roomId) {
        Map<String, Session> sessions = roomSessions.get(roomId);
        return sessions != null ? sessions.size() : 0;
    }

    /**
     * 查找可用座位
     *
     * @param roomUserService RoomUserService实例
     * @param roomId          房间ID
     * @return 可用座位编号
     */
    private int findAvailableSeat(RoomUserService roomUserService, String roomId) {
        LambdaQueryWrapper<RoomUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoomUser::getRoomId, roomId);
        List<RoomUser> usersInRoom = roomUserService.list(queryWrapper);

        // 创建座位占用情况数组，索引代表座位号，值代表是否被占用
        boolean[] seatsOccupied = new boolean[4];

        // 标记已被占用的座位
        for (RoomUser user : usersInRoom) {
            if (user.getLocation() != null && user.getLocation() >= 0 && user.getLocation() < 4) {
                seatsOccupied[user.getLocation()] = true;
            }
        }

        // 查找第一个空闲座位
        for (int i = 0; i < 4; i++) {
            if (!seatsOccupied[i]) {
                return i;
            }
        }

        // 如果没有找到空闲座位，默认返回0
        return 0;
    }

}
