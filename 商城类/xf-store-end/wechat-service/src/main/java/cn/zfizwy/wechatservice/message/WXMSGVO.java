package cn.zfizwy.wechatservice.message;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WXMSGVO {
    private String openId;
    private  String templateId;
    private  String page;
    private Map<String,TemplateDataVO> data;
}
