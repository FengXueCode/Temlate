package cn.zfizwy.xfmahjongend.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SeatVO {
    private String userId;
    private String toUserId;
    private int newLocation;
}
