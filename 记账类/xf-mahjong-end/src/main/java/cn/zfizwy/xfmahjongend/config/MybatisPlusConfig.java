package cn.zfizwy.xfmahjongend.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 表示这是一个配置类，通常用于配置Spring框架的各种组件
public class MybatisPlusConfig {
    /**
     * 创建并配置MybatisPlusInterceptor拦截器。
     * 这个方法没有参数。
     * @return 返回配置好的MybatisPlusInterceptor实例。
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件，支持MySQL数据库分页
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}

