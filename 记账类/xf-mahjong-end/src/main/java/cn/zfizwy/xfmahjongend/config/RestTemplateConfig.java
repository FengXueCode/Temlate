package cn.zfizwy.xfmahjongend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
/**
 * RestTemplateConfig类用于配置RestTemplate请求
 */
public class RestTemplateConfig {

    /**
     * 创建并返回一个RestTemplate实例。
     * 该方法没有参数。
     *
     * @return RestTemplate 新建的RestTemplate实例
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
