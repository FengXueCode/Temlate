package cn.zfizwy.wechatservice;


import cn.zfizwy.commonservice.config.DefaultFeignConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableFeignClients(defaultConfiguration = DefaultFeignConfig.class)
public class WechatServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WechatServiceApplication.class, args);
    }

}
