package cn.zfizwy.xfmahjongend.service;

import cn.zfizwy.xfmahjongend.common.R;
import cn.zfizwy.xfmahjongend.entity.Room;
import cn.zfizwy.xfmahjongend.entity.RoomRecord;
import cn.zfizwy.xfmahjongend.entity.RoomUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
* @author ChengLexiang
* @description 针对表【room】的数据库操作Service
* @createDate 2025-09-22 18:00:30
*/
public interface RoomService extends IService<Room> {

    R createRoom( int mode, BigDecimal ratio);


    R getRoom();

    List<RoomUser> getRoomUser(String roomId);

    byte[] getRoomQrCode(String roomId);


    R checkFriend(String roomId);


    R updateTea(Room room);

    R updateRemark(String to, String remark);

    List<RoomRecord> getRoomRecord(String roomId);

    R getTea(String roomId);

    List<RoomRecord> getSettlement(String roomId);
}
