package cn.zfizwy.commonservice.utils;

public class UserContext {
    private static final ThreadLocal<String> tl = new ThreadLocal<>();
    public static void set(String userId) {
        tl.set(userId);
    }
    public static String get() {
        return tl.get();
    }
    public static void remove() {
        tl.remove();
    }
}
