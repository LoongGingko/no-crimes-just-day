package com.loogingko.ncjd;

import com.loogingko.ncjd.config.JwtProperties;
import com.loogingko.ncjd.config.NCJDProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan("com.loogingko.ncjd.mapper")
@EnableConfigurationProperties({NCJDProperties.class, JwtProperties.class})
public class Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(Application.class, args);
        System.out.println("""
        
        ╔═══════════════════════════════════════════════╗
        ║                                               ║
        ║   ███╗   ██╗ ██████╗      ██╗██████╗          ║
        ║   ████╗  ██║██╔════╝      ██║██╔══██╗         ║
        ║   ██╔██╗ ██║██║           ██║██║  ██║         ║
        ║   ██║╚██╗██║██║      ██   ██║██║  ██║         ║
        ║   ██║ ╚████║╚██████╗ ╚█████╔╝██████╔╝         ║
        ║   ╚═╝  ╚═══╝ ╚═════╝  ╚════╝ ╚═════╝          ║
        ║                                               ║
        ║              启动成功，宝贝儿                    ║
        ╚═══════════════════════════════════════════════╝
        
        """);
    }
}
