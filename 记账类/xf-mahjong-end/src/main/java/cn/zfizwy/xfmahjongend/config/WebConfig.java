package cn.zfizwy.xfmahjongend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
/**
 * WebConfig类实现了WebMvcConfigurer接口，
 * 用于配置Web应用，特别是通过Tomcat服务器访问本地文件系统中的图片资源。
 */
public class WebConfig implements WebMvcConfigurer {

    /**
     * 该方法用于添加资源处理器，以使Spring MVC能够处理静态资源请求。
     *
     * @param registry 资源处理器注册表，用于注册资源处理器和资源位置。
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String url = "file:/D:/imgs/";
//        String url = "file:/opt/imgs/";
        // 设置资源访问的URL前缀，指定图片资源位于D盘imgs文件夹下
        registry.addResourceHandler("/images/**").addResourceLocations(url);
    }
}
