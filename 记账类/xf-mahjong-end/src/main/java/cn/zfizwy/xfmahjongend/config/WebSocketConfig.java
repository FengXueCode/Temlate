package cn.zfizwy.xfmahjongend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfig {
    /**
     * 创建并返回一个ServerEndpointExporter的实例。
     * 这个Bean负责自动注册使用@ServerEndpoint注解标记的WebSocket端点。
     *
     * @return ServerEndpointExporter - 用于自动注册WebSocket端点的Bean
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
