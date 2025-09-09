package cn.zfizwy.killquestionend.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 一个泛型类R，用于封装响应信息。
 * @param <T> 响应数据的类型。
 */
public class R<T> {
    private int code; // 响应状态码
    private String msg; // 响应消息
    private T data; // 响应数据

}
