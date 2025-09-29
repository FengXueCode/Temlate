package cn.zfizwy.xfmahjongend.controller;

import cn.zfizwy.xfmahjongend.entity.Room;
import cn.zfizwy.xfmahjongend.entity.RoomRecord;
import cn.zfizwy.xfmahjongend.entity.RoomUser;
import cn.zfizwy.xfmahjongend.entity.User;
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
        queryWrapper1.eq(RoomUser::getRoomId, roomId).eq(RoomUser::getStatus,1);
        List<RoomUser> list = roomUserService.list(queryWrapper1);


        if (one == null) {
            if(list.size()== (room.getRoomType()==0?4:8)){
                sendMsg(session, "房间已满");
                return;
            }
            RoomUser roomUser = new RoomUser();
            roomUser.setRoomId(roomId);
            roomUser.setUserId(userId);
            roomUser.setNickname(user.getNickname());
            roomUser.setPortrait(user.getPortrait());
            //判断座位
            if(room.getRoomType()==0){
                int location = findAvailableSeat(roomUserService, roomId);
                roomUser.setLocation(location);
            }

            roomUserService.save(roomUser);
        }else{
            if(one.getStatus()==0){
               one.setMoney(BigDecimal.ZERO);
            }
            one.setStatus(1);
            roomUserService.update(one, queryWrapper);
        }
        broadcastToAllInRoom(roomId, user.getNickname() + "加入房间");

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
            settlement(roomId,userId,true);
        }
        //交互座位
        if(message.contains("切换座位")){
            changeSeat(roomId, message);
        }
        //房间结算
        if (message.contains("房间结算")) {
            roomSettlement(roomId);
        }

    }
    //切换座位
    private void changeSeat(String roomId, String message){

        //将message使用fastjson转换为解析为TransferVO对象
        SeatVO seatVO = JSON.parseObject(message.split("=")[1], SeatVO.class);
        System.out.println("seatVO = " + seatVO);
        RoomUserService roomUserService = applicationContext.getBean(RoomUserService.class);

        if(seatVO.getToUserId().isEmpty()){
            LambdaUpdateWrapper<RoomUser> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(RoomUser::getRoomId, roomId)
                    .eq(RoomUser::getUserId, seatVO.getUserId())
                    .set(RoomUser::getLocation, seatVO.getNewLocation());
             roomUserService.update(null, updateWrapper);
        }else {
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
    protected void settlement(String roomId,String userId,Boolean flag) {
        //flag true 个人结算 false 房间结算


        RoomUserService roomUserService = applicationContext.getBean(RoomUserService.class);
        RoomRecordService roomRecordService = applicationContext.getBean(RoomRecordService.class);

        // 获取最新的结算记录时间
        LambdaQueryWrapper<RoomRecord> settlementQueryWrapper = new LambdaQueryWrapper<>();
        settlementQueryWrapper.eq(RoomRecord::getRoomId, roomId)
                .eq(RoomRecord::getRecordType, 1)
                .eq(RoomRecord::getSettlement,userId)
                .orderByDesc(RoomRecord::getCreateTime)
                .last("LIMIT 1");

        RoomRecord lastSettlementRecord = roomRecordService.getOne(settlementQueryWrapper);

        // 获取该用户作为转账方或收款方的所有记录
        LambdaQueryWrapper<RoomRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoomRecord::getRoomId, roomId)
                .eq(RoomRecord::getRecordType, 0)
                .and(wrapper -> wrapper.eq(RoomRecord::getWinner, userId)
                        .or()
                        .eq(RoomRecord::getLoser, userId));

        //判断结算是否存在
        if (lastSettlementRecord!=null) {
            //获取在roomRecord时间后面创建的记录
            queryWrapper.ge(RoomRecord::getCreateTime, lastSettlementRecord.getCreateTime());
        }

        List<RoomRecord> records = roomRecordService.list(queryWrapper);

        //        获取当前用户
        LambdaQueryWrapper<RoomUser> queryUserWrapper = new LambdaQueryWrapper<>();
        queryUserWrapper.eq(RoomUser::getRoomId, roomId).eq(RoomUser::getUserId, userId);
        RoomUser user = roomUserService.getOne(queryUserWrapper);



        // 计算与每个人的结余
        Map<String, BigDecimal> balanceMap = new ConcurrentHashMap<>();
        for (RoomRecord record : records) {
            String otherUserId;
            BigDecimal amount = record.getMoney();

            if (record.getWinner().equals(userId)) {
                // 用户是收款方，增加结余
                otherUserId = record.getLoser();
            } else {
                // 用户是转账方，减少结余（金额为负）
                otherUserId = record.getWinner();
                amount = amount.negate();
            }
            // 累加结余金额
            balanceMap.put(otherUserId, balanceMap.getOrDefault(otherUserId, BigDecimal.ZERO).add(amount));

        }

        // 遍历结余结果
        for (Map.Entry<String, BigDecimal> entry : balanceMap.entrySet()) {
            String other = entry.getKey();
            BigDecimal balance = entry.getValue();
            if(balance.compareTo(BigDecimal.ZERO) == 0){
                continue;
            }
            //获取对方用户
            LambdaQueryWrapper<RoomUser> queryOtherWrapper = new LambdaQueryWrapper<>();
            queryOtherWrapper.eq(RoomUser::getRoomId, roomId).eq(RoomUser::getUserId, other);
            RoomUser otherUser = roomUserService.getOne(queryOtherWrapper);

            //记录结算记录
            RoomRecord roomRecord = new RoomRecord();
            //判断结余是否大于0
            if (balance.compareTo(BigDecimal.ZERO) > 0) {
                roomRecord.setWinner(userId);
                roomRecord.setLoser(other);


            } else {
                roomRecord.setWinner(other);
                roomRecord.setLoser(userId);
            }
            roomRecord.setSettlement(user.getUserId());
            roomRecord.setMoney(balance);
            roomRecord.setRoomId(roomId);
            roomRecord.setRecordType(1);
            roomRecord.setSettlementLocation(user.getLocation());

            boolean save = roomRecordService.save(roomRecord);


            if(flag){
                otherUser.setMoney(otherUser.getMoney().add(balance));
                user.setMoney(user.getMoney().subtract(balance));
                boolean updateOther = roomUserService.update(otherUser, queryOtherWrapper);

            }

            user.setStatus(0);
            boolean updateUser = roomUserService.update(user, queryUserWrapper);

            if (save  && updateUser&&flag) {
                //获取保存后的roomRecord
                roomRecord = roomRecordService.getById(roomRecord.getRoomRecordId());
                broadcastToAllInRoom(roomId, "个人结算结果=" + JSON.toJSONString(roomRecord));
                broadcastToAllInRoom(roomId, "刷新用户数据");
            }

        }

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
     * @param roomUserService RoomUserService实例
     * @param roomId 房间ID
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

    //房间结算
    protected void roomSettlement(String roomId) {

        RoomUserService roomUserService = applicationContext.getBean(RoomUserService.class);

        LambdaQueryWrapper<RoomUser> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(RoomUser::getRoomId, roomId).eq(RoomUser::getStatus, 1) ;

      // 1.获取所有在线用户
        List<RoomUser> roomUsers = roomUserService.list(userQueryWrapper);
      // 2. 遍历用户 与 其它用户结算
      for (RoomUser roomUser : roomUsers) {
          settlement(roomId, roomUser.getUserId(),false);
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
        RoomService roomService = applicationContext.getBean(RoomService.class);
        LambdaUpdateWrapper<Room> roomUpdateWrapper = new LambdaUpdateWrapper<>();
        roomUpdateWrapper.eq(Room::getRoomId, roomId).set(Room::getStatus, 0);
        roomService.update(roomUpdateWrapper);
        broadcastToAllInRoom(roomId,"房间结算");

    }
}
