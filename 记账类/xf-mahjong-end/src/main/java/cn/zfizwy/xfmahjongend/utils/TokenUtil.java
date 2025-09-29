package cn.zfizwy.xfmahjongend.utils;

import cn.zfizwy.xfmahjongend.common.R;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenUtil {
    //token过期时间
    private static final long EXPIRE_TIME =7 * 24* 60 * 60 * 1000;
    //token密钥
    private static final String TOKEN_SECRET = "FengXue";

  public static String sign(String userId) {
    try {
        // 设置过期时间为一周后
        Date expiresAt = Date.from(ZonedDateTime.now().plusWeeks(1).toInstant());

        // 私钥和加密算法
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
        // 设置头部信息
        Map<String, Object> header = new HashMap<>(2);
        header.put("Type", "Jwt");
        header.put("alg", "HS256");
        // 生成东八区的当前时间
        Date loginTime = Date.from(ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toInstant());

        // 返回token字符串
        return JWT.create()
                .withHeader(header)
                .withClaim("userId", userId)
                .withClaim("loginTime", loginTime)
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    } catch (JWTCreationException e) {
        e.printStackTrace();
        return null;
    }
}

    public static String getUserId(String token) {
        try {
            String userId = JWT.decode(token).getClaim("userId").asString();
            return userId;
        } catch (Exception e) {
            return null;
        }
    }
    public static R verify(String token){
        try {
            //设置签名的加密算法：HMAC256
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return new R<>(200,"验证成功",true);
        } catch (TokenExpiredException e) {
            // 处理 token 过期的情况
           return new R<>(1001,"token已过期",false);

        }catch (Exception e){
            return new R<>(1002,"token无效",false);
        }
    }
}
