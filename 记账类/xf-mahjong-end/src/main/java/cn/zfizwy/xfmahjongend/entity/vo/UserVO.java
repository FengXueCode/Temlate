package cn.zfizwy.xfmahjongend.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserVO {
    private String userId;
    private String nickName;
    private String portrait;
    private String token;
}
