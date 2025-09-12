package cn.zfizwy.wechatservice.conrtoller;

import cn.zfizwy.wechatservice.message.SendMessage;
import cn.zfizwy.wechatservice.message.TemplateDataVO;
import cn.zfizwy.wechatservice.message.WXMSGVO;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/wechat/message")
public class MessageController {
    //社团审核结果模板
    private  final String CLUB_AUDIT_TEMPLATE_ID ="kTVDgEbnmVU8h5_bZLwkYIlLsuQ89ye4LhbE9WORUNU";
    private final String OPEN_DRAW_TEMPLATE_ID = "vgynmB0io3jAxLPsbrd19lywY9CQashA3Nhnsx4q6Es";

    //开奖通知
    @PostMapping("/openDraw")
    public void openDraw(@RequestParam("openId") String openId, @RequestParam("luckDrawId") String luckDrawId, @RequestParam("awardName") String awardName, @RequestParam("desc") String desc, @RequestParam("title") String title) {
        WXMSGVO wxmsgvo = new WXMSGVO();
        wxmsgvo.setOpenId(openId);
        wxmsgvo.setTemplateId(OPEN_DRAW_TEMPLATE_ID);
        String page = "pageIndex/index/tools/luckDraw/drawDetail?item=" + luckDrawId;
        wxmsgvo.setPage(page);
        Map<String, TemplateDataVO> data = new HashMap<>();
        data.put("thing6", new TemplateDataVO(title));
        data.put("thing3", new TemplateDataVO(awardName));
        data.put("thing8", new TemplateDataVO(desc));
        wxmsgvo.setData(data);
        SendMessage.sendTemplateMessage(wxmsgvo);
    }

    //社团申请
    @PostMapping("/clubAudit")
    public void clubAudit(@RequestParam("openId") String openId, @RequestParam("clubName") String clubName, @RequestParam("response") String response) {
        WXMSGVO wxmsgvo = new WXMSGVO();
        wxmsgvo.setOpenId(openId);
        wxmsgvo.setTemplateId(CLUB_AUDIT_TEMPLATE_ID);
        wxmsgvo.setPage("pages/index/index");
        Map<String, TemplateDataVO> data = new HashMap<>();
        data.put("thing1", new TemplateDataVO(clubName));
        data.put("phrase3", new TemplateDataVO(response));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        data.put("time4", new TemplateDataVO(dateFormat.format(new Date())));
        wxmsgvo.setData(data);
        SendMessage.sendTemplateMessage(wxmsgvo);
    }
}
