package cn.zfizwy.wechatservice.conrtoller;

import cn.zfizwy.wechatservice.config.Constant;
import cn.zfizwy.wechatservice.pay.PayVO;
import cn.zfizwy.wechatservice.pay.WechatPayUtil;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.exception.ValidationException;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction;
import com.wechat.pay.java.service.refund.model.Refund;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@CrossOrigin
@RequestMapping("/wechat/pay")
public class PayController {
    private static final Logger logger = LoggerFactory.getLogger(PayController.class);

    //微信支付
    @GetMapping("/prepay")
    public PayVO prepay(@RequestParam Double total, @RequestParam String description, @RequestParam String openId, @RequestParam String url) {

        return WechatPayUtil.prepayWithRequestPayment(total, description, openId, url);
    }


    //微信退款
    @GetMapping("/refund")
    public Refund refund(@RequestParam String outTradeNo, @RequestParam Double total, @RequestParam String reason) {
        return WechatPayUtil.refund(outTradeNo, total, reason);
    }


    //支付回调
//    @PostMapping("/notify/expressage")
//    public ResponseEntity<Void> expressageCallback(@RequestHeader("Wechatpay-Serial") String wechatPaySerial,
//                                                    @RequestHeader("Wechatpay-Nonce") String wechatpayNonce,
//                                                    @RequestHeader("Wechatpay-Signature") String wechatSignature,
//                                                    @RequestHeader("Wechatpay-Timestamp") String wechatTimestamp,
//                                                    @RequestBody String requestBody) {
//        com.wechat.pay.java.core.notification.RequestParam requestParam = new com.wechat.pay.java.core.notification.RequestParam.Builder()
//                .serialNumber(wechatPaySerial)
//                .nonce(wechatpayNonce)
//                .signature(wechatSignature)
//                .timestamp(wechatTimestamp)
//                .body(requestBody)
//                .build();
//
//        Config config = new RSAAutoCertificateConfig.Builder()
//                .merchantId(Constant.MERCHANT_ID)
//                .privateKeyFromPath(Constant.PRIVATE_KEY_PATH)
//                .merchantSerialNumber(Constant.MERCHANT_SERIAL_NUMBER)
//                .apiV3Key(Constant.API_V3_KEY)
//                .build();
//        NotificationParser parser = new NotificationParser((NotificationConfig) config);
//        try {
//            // 3. 解析并验证通知（自动验签+解密）
//            Transaction transaction = parser.parse(requestParam, Transaction.class);
//
//            // 4. 业务处理（需实现幂等性）
//            String outTradeNo = transaction.getOutTradeNo();
//            lifeClient.payExpressageSuccess(outTradeNo);
//
//            return ResponseEntity.ok().build();
//
//        } catch (Exception e) {
//            // 5. 异常处理（区分验证失败和业务错误）
//            return handleException(e, requestBody);
//        }
//    }


    /**
     * 异常统一处理
     */
    private ResponseEntity<Void> handleException(Exception e, String requestBody) {
        if (e instanceof ValidationException) {
            // 签名验证失败（可能被篡改）
            logger.error("回调签名验证失败 | 请求体: {}", requestBody, e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } else {
            // 业务处理失败（返回5xx会触发微信重试）
            logger.error("回调处理异常 | 请求体: {}", requestBody, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
