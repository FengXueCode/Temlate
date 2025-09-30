package cn.zfizwy.xfmahjongend.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SettlementVO {
    private String winner;
    private String loser;
    private BigDecimal money;
}
