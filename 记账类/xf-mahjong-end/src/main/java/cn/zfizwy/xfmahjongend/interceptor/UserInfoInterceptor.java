package cn.zfizwy.xfmahjongend.interceptor;

import cn.zfizwy.xfmahjongend.utils.TokenUtil;
import cn.zfizwy.xfmahjongend.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.List;

public class UserInfoInterceptor implements HandlerInterceptor {

    private List<String> excludePaths = new ArrayList<>();
    private AntPathMatcher pathMatcher = new AntPathMatcher();

    public UserInfoInterceptor() {
        // 默认放行的路径
        excludePaths.add("/images/**");
        excludePaths.add("/room/settlement");
        excludePaths.add("/room/roomSettlement");
        excludePaths.add("/websocket/**");
        excludePaths.add("/user/init");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        // 检查当前请求是否在放行列表中
        for (String excludePath : excludePaths) {
            if (pathMatcher.match(excludePath, requestURI)) {
                return true; // 直接放行
            }
        }
        
        // 处理OPTIONS预检请求，避免跨域问题
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
        
        // 1.获取请求头中的用户信息
        String userInfo = TokenUtil.getUserId(request.getHeader("authorization"));
        // 2.判断是否为空
        if (userInfo != null && !userInfo.isEmpty()) {
            UserContext.set(userInfo);

        }

        else {
            // 用户信息为空，返回未授权状态
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        // 3.放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除用户
        UserContext.remove();
    }

    public void setExcludePaths(List<String> excludePaths) {
        this.excludePaths = excludePaths;
    }
}