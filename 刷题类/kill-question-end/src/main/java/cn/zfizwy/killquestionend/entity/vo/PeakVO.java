package cn.zfizwy.killquestionend.entity.vo;

import cn.zfizwy.killquestionend.entity.Peak;
import cn.zfizwy.killquestionend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PeakVO {
    private Peak peak;
    private User user;
}
