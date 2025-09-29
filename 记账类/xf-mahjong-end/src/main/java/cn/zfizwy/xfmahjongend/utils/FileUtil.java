package cn.zfizwy.xfmahjongend.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class FileUtil {
    //服务器路径用于访问图片
//    public static String BASE_URL = "http://backend.storyxz.top";
//    public static String BASE_URL = "http://127.0.0.1:8080";
    public static String BASE_URL = "http://10.8.131.19:8080";
//    public static String BASE_URL = "http://172.18.0.2:8080";
// 用户头像路径
    public static String PORTRAIT = "D:\\imgs\\portrait";
//    public static String PORTRAIT = "/opt/imgs/portrait";

    public static String savePortrait(MultipartFile file) {
        String path = PORTRAIT + "/";
        String type = "/portrait/";
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

        path = "D:\\imgs" + images[1];
//        path = "/opt/imgs" + images[1];
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
