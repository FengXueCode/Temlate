package cn.zfizwy.killquestionend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.zfizwy.killquestionend.mapper")
public class KillQuestionEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(KillQuestionEndApplication.class, args);
    }

}
