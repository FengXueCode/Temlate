package cn.zfizwy.commonservice.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class FileUtil {
    //服务器路径用于访问图片
    public static String BASE_URL = "http://backend.storyxz.top";
//    public static String BASE_URL = "http://172.18.0.2:8080";
//    //        首页轮播图路径
//    public static String BANNER = "D:\\imgs\\banner";
//    //    //商品图片路径
//    public static String COMMODITY = "D:\\imgs\\commodity";
//    //    //    用户头像路径
//    public static String PORTRAIT = "D:\\imgs\\portrait";
//    //    //学生认证路径
//    public static String VERIFY = "D:\\imgs\\verify";
//    //    //订单完成路径
//    public static String FINISH = "D:\\imgs\\finish";
//    //    //菜单
//    public static String MENU = "D:\\imgs\\menu";
//    //    //marker
//    public static String MARKER = "D:\\imgs\\marker";
//    // markdown
//    public static String MARKDOWN = "D:\\imgs\\markdown";
//    //表白墙
//    public static String FORUM = "D:\\imgs\\forum";
//    //幸运抽奖
//    public static String LUCKDRAW = "D:\\imgs\\luckDraw";
//    //社团
//    public static String CLUB = "D:\\imgs\\club";


    public static String BANNER = "/opt/imgs/banner";
    public static String COMMODITY = "/opt/imgs/commodity";
    public static String PORTRAIT = "/opt/imgs/portrait";
    public static String VERIFY = "/opt/imgs/verify";
    public static String FINISH = "/opt/imgs/finish";
    public static String MENU = "/opt/imgs/menu";
    public static String MARKER = "/opt/imgs/marker";
    public static String MARKDOWN = "/opt/imgs/markdown";
    public static String FORUM = "/opt/imgs/forum";
    public static String LUCKDRAW = "/opt/imgs/luckDraw";
    public static String CLUB = "/opt/imgs/club";

    public static String saveClub(MultipartFile file) {
        String time = new Date().getTime() + "";
        String path = CLUB + "/" + time + "/";
        String type = "/club/" + time + "/";
        return save(file, path, type);
    }

    public static String saveLuckDraw(MultipartFile file) {
        String time = new Date().getTime() + "";
        String path = LUCKDRAW + "/" + time + "/";
        String type = "/luckDraw/" + time + "/";
        return save(file, path, type);
    }

    public static String saveForum(MultipartFile file, String forumId) {
        String path = FORUM + "/" + forumId + "/";
        String type = "/forum/" + forumId + "/";
        return save(file, path, type);
    }

    public static String saveMarkdown(MultipartFile file) {
        String time = new Date().getTime() + "";
        String path = MARKDOWN + "/" + time + "/";
        String type = "/markdown/" + time + "/";
        return save(file, path, type);
    }

    public static String saveMarker(MultipartFile file) {
        String time = new Date().getTime() + "";
        String path = MARKER + "/" + time + "/";
        String type = "/marker/" + time + "/";
        return save(file, path, type);
    }

    public static String saveMenu(MultipartFile file) {
        String time = new Date().getTime() + "";
        String path = MENU + "/" + time + "/";
        String type = "/menu/" + time + "/";
        return save(file, path, type);
    }

    public static String saveBanner(MultipartFile file) {
        String time = new Date().getTime() + "";
        String path = BANNER + "/" + time + "/";
        String type = "/banner/" + time + "/";
        return save(file, path, type);
    }

    public static String saveCommodity(MultipartFile file, String commodityId) {
        String path = COMMODITY + "/" + commodityId + "/";
        String type = "/commodity/" + commodityId + "/";
        return save(file, path, type);
    }

    public static String savePortrait(MultipartFile file) {
        String path = PORTRAIT + "/";
        String type = "/portrait/";
        return save(file, path, type);
    }

    public static String saveVerify(MultipartFile file, String userId) {
        String path = VERIFY + "/" + userId + "/";
        String type = "/verify/" + userId + "/";
        return save(file, path, type);
    }

    public static String saveFinish(MultipartFile file, String orderId) {
        String path = FINISH + "/" + orderId + "/";
        String type = "/finish/" + orderId + "/";
        return save(file, path, type);
    }

    public static String save(MultipartFile file, String filePath, String type) {
        String fileName = file.getOriginalFilename();
        File dest = new File(filePath + fileName);
        if (!dest.exists()) {
            try {
                dest.mkdirs();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        try {
            file.transferTo(dest);
            return BASE_URL + "/images" + type + fileName;
        } catch (IOException e) {
            System.out.println(e);
            return "上传失败";
        }
    }

    public static boolean remove(String path) {
        String[] images = path.split("images");

//        path = "D:\\imgs" + images[1];
        path = "/opt/imgs" + images[1];
        System.out.println("路径" + path);
        File file = new File(path);
        if (file.exists()) {
            file.delete();
            return true;

        } else {
            return false;
        }

    }


}
