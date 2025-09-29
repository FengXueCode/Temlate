package cn.zfizwy.xfmahjongend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.zfizwy.xfmahjongend.mapper")
public class XfMahjongEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(XfMahjongEndApplication.class, args);
    }

}
