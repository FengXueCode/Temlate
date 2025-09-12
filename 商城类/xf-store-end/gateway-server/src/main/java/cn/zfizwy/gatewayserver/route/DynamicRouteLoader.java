package cn.zfizwy.gatewayserver.route;

import cn.zfizwy.gatewayserver.route.filter.AuthGlobalFilter;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

import cn.hutool.json.JSONUtil;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicRouteLoader implements ApplicationEventPublisherAware {
    // 路由配置文件的id和分组
    private final String dataId = "gateway-routes.json";
    private final String authDataId = "auth-config.json";
    private final String group = "DEFAULT_GROUP";
    @Autowired
    RouteDefinitionWriter writer;
    @Autowired
    NacosConfigManager nacosConfigManager;
    @Autowired
    AuthGlobalFilter authGlobalFilter;

    // 保存更新过的路由id
    private final Set<String> routeIds = new HashSet<>();
    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    @PostConstruct
    public void initRouteConfigListener() throws NacosException {
        // 1.注册路由配置监听器并首次拉取配置
        String configInfo = nacosConfigManager.getConfigService()
                .getConfigAndSignListener(dataId, group, 5000, new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return null;
                    }

                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        updateConfigInfo(configInfo);
                    }
                });

        // 2.注册认证配置监听器并首次拉取配置
        String authConfigInfo = nacosConfigManager.getConfigService()
                .getConfigAndSignListener(authDataId, group, 5000, new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return null;
                    }

                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        updateAuthConfigInfo(configInfo);
                    }
                });
        // 2.首次启动时，更新一次配置
        updateConfigInfo(configInfo);
        updateAuthConfigInfo(authConfigInfo);
    }

    private void updateConfigInfo(String configInfo) {
        log.debug("监听到路由配置变更，{}", configInfo);
        // 1.反序列化
        List<RouteDefinition> routeDefinitions = JSONUtil.toList(configInfo, RouteDefinition.class);

        // 2.更新前先清空旧路由
        for (String routeId : routeIds) {
            writer.delete(Mono.just(routeId)).subscribe();
        }
        routeIds.clear();

        // 2.2.判断是否有新的路由要更新
        if (routeDefinitions.isEmpty()) {
            return;
        }

        // 3.更新路由
        routeDefinitions.forEach(routeDefinition -> {
            // 检查是否为 WebSocket 路由
            if (isWebSocketRoute(routeDefinition)) {
                // 添加 WebSocket 支持
                addWebSocketSupport(routeDefinition);
            }

            // 保存路由
            writer.save(Mono.just(routeDefinition)).subscribe();
            routeIds.add(routeDefinition.getId());
        });

        // 发布路由刷新事件
        publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    private boolean isWebSocketRoute(RouteDefinition routeDefinition) {
        // 检查路由元数据中是否标记为 websocket
        return "true".equals(routeDefinition.getMetadata().get("websocket"));
    }

    private void addWebSocketSupport(RouteDefinition routeDefinition) {
        // 确保包含 WebSocket 的 predicates 和 filters
        // 可以根据需要添加特定的 WebSocket 处理逻辑

    }


    private void updateAuthConfigInfo(String configInfo) {
        log.debug("监听到认证配置变更，{}", configInfo);
        try {
            // 假设JSON格式为 { "excludePaths": ["/path1", "/path2"] }
            cn.hutool.json.JSONObject jsonObject = cn.hutool.json.JSONUtil.parseObj(configInfo);
            List<String> excludePaths = jsonObject.getBeanList("excludePaths", String.class);
            if (excludePaths != null) {
                authGlobalFilter.updateExcludePaths(excludePaths);
            }
        } catch (Exception e) {
            log.error("解析认证配置失败", e);
        }
    }
}
