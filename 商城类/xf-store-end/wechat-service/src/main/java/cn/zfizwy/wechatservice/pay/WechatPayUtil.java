/*
 * @Author: FengXue
 * @Date: 2024-06-04 15:00:50
 * @LastEditors: FengXue
 * @LastEditTime: 2024-08-06 17:20:45
 * @filePath: Do not edit
 */
package cn.zfizwy.wechatservice.pay;

import cn.zfizwy.wechatservice.config.Constant;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.AmountReq;
import com.wechat.pay.java.service.refund.model.CreateRequest;
import com.wechat.pay.java.service.refund.model.Refund;

import java.util.UUID;


public class WechatPayUtil {


    /**
     * 初始化配置
     */
    public static Config config = new RSAAutoCertificateConfig.Builder().merchantId(Constant.MERCHANT_ID).privateKeyFromPath(Constant.PRIVATE_KEY_PATH).merchantSerialNumber(Constant.MERCHANT_SERIAL_NUMBER).apiV3Key(Constant.API_V3_KEY).build();
    /**
     * 服务
     */
    public static JsapiServiceExtension service = new JsapiServiceExtension.Builder().config(config).build();

    /**
     * 退款服务
     */
    public static RefundService refundService = new RefundService.Builder().config(config).build();

    /**
     * 预支付
     */
    public static PayVO prepayWithRequestPayment(Double total, String description, String openid,String url) {
        total = total * 100;
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        Payer payer = new Payer();
        payer.setOpenid(openid);
        amount.setTotal(total.intValue());
        request.setAmount(amount);
        request.setAppid(Constant.APP_ID);
        request.setMchid(Constant.MERCHANT_ID);
        request.setNotifyUrl(url);
        request.setDescription(description);
        String outTradeNo = UUID.randomUUID().toString().replace("-","");
        request.setOutTradeNo(outTradeNo);
        request.setPayer(payer);
        return new PayVO("", outTradeNo, service.prepayWithRequestPayment(request));
    }

    /**
     * 退款
     */
    public static Refund refund(String outTradeNo, Double total, String reason) {
        total = total * 100;
        AmountReq amount = new AmountReq();
        amount.setTotal(total.longValue());
        amount.setRefund(total.longValue());
        amount.setCurrency("CNY");
        CreateRequest request = new CreateRequest();
        request.setAmount(amount);
        request.setOutTradeNo(outTradeNo);
        request.setOutRefundNo(UUID.randomUUID().toString().replace("-",""));
        request.setReason(reason);
        return refundService.create(request);
    }

}
