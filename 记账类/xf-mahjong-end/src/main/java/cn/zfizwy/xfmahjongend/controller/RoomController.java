package cn.zfizwy.xfmahjongend.controller;

import cn.zfizwy.xfmahjongend.common.R;
import cn.zfizwy.xfmahjongend.entity.Room;
import cn.zfizwy.xfmahjongend.entity.RoomRecord;
import cn.zfizwy.xfmahjongend.entity.RoomUser;
import cn.zfizwy.xfmahjongend.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/room")
@CrossOrigin
public class RoomController {
    //创建房间
    @Autowired
    RoomService roomService;

    @GetMapping("/create")
    public R createRoom(@RequestParam("mode") int mode, @RequestParam("ratio") BigDecimal  ratio) {
        return roomService.createRoom(  mode,  ratio);
    }

    @GetMapping("/getRoom")
    public R getRoom() {
        return roomService.getRoom();
    }

    @GetMapping("/getRoomById")
    public R getRoomById(@RequestParam("roomId") String roomId) {
        return new R<>(200, "查询成功", roomService.getById(roomId));
    }

    //修改房间茶水
    @PostMapping("/updateTea")
    public R updateTea(@RequestBody Room room) {
        return roomService.updateTea(room);
    }


    //查找房间用户
    @GetMapping("/getRoomUser")
    public List<RoomUser> getRoomUser(@RequestParam("roomId") String roomId) {
        return roomService.getRoomUser(roomId);
    }

    //获取房间分享二维码
    @GetMapping("/getRoomQrCode")
    public byte[] getRoomQrCode(@RequestParam("roomId") String roomId) {
        return roomService.getRoomQrCode(roomId);
    }


    //检查当前房间内的用户是否为好友，不是添加为好友
    @GetMapping("/checkFriend")
    public R checkFriend(@RequestParam("roomId") String roomId) {
        return roomService.checkFriend(roomId);
    }


    //修改好友备注
    @GetMapping("/updateRemark")
    public R updateRemark( @RequestParam("to") String to, @RequestParam("remark") String remark) {
        return roomService.updateRemark(to, remark);
    }

    //查找房间记录
    @GetMapping("/getHistory")
    public List<RoomRecord> getRoomRecord(@RequestParam("roomId") String roomId) {
        return roomService.getRoomRecord(roomId);
    }
    //获取房间茶水
    @GetMapping("/getTea")
    public R getTea(@RequestParam("roomId") String roomId){
        return roomService.getTea(roomId);
    }

    //获取结算信息
    @GetMapping("/getSettlement")
public List<RoomRecord> getSettlement(@RequestParam("roomId") String roomId){
        return roomService.getSettlement(roomId);
    }
}
