package cn.zfizwy.wechatservice.pay;

import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PayVO {
    private String id;
    private String outTradeNo;
    private PrepayWithRequestPaymentResponse response;
}
