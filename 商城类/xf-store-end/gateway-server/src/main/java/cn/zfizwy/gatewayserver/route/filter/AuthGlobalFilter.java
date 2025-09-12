package cn.zfizwy.gatewayserver.route.filter;

import cn.zfizwy.commonservice.utils.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
public class

AuthGlobalFilter implements GlobalFilter {
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    // 存储动态排除路径
    private final List<String> excludePaths = new CopyOnWriteArrayList<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.获取Request
        ServerHttpRequest request = exchange.getRequest();
        // 2.判断是否不需要拦截
        if (isExclude(request.getPath().toString())) {
            // 无需拦截，直接放行
            return chain.filter(exchange);
        }
        // 3.获取请求头中的token
        List<String> headers = request.getHeaders().get("authorization");
        String userId = null;
        try {
            if (headers != null && !headers.isEmpty()) {
                String token = headers.get(0);
                // 去除Bearer前缀（如果有的话）
                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }
                userId = TokenUtil.getUserId(token);
            }

            if (userId == null) {
                // 如果无效，拦截
                ServerHttpResponse response = exchange.getResponse();
                response.setRawStatusCode(401);
                return response.setComplete();
            }
        } catch (Exception e) {
            // 如果解析失败，拦截
            ServerHttpResponse response = exchange.getResponse();
            response.setRawStatusCode(401);
            return response.setComplete();
        }


        // TODO 5.如果有效，传递用户信息

        String userInfo = userId;
        ServerWebExchange ex = exchange.mutate()
                .request(b -> b.header("user-info", userInfo))
                .build();
        // 6.放行
        return chain.filter(ex);
    }

    private boolean isExclude(String antPath) {
        for (String pathPattern : excludePaths) {
            if (antPathMatcher.match(pathPattern, antPath)) {
                return true;
            }
        }
        return false;
    }

    // 提供方法用于更新排除路径
    public void updateExcludePaths(List<String> paths) {
        this.excludePaths.clear();
        this.excludePaths.addAll(paths);
    }

    // 获取当前排除路径
    public List<String> getExcludePaths() {
        return new CopyOnWriteArrayList<>(this.excludePaths);
    }

}
