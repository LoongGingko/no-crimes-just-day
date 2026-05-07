package com.loogingko.ncjd;

import com.loogingko.ncjd.model.entity.User;
import com.loogingko.ncjd.service.auth.JwtService;
import com.loogingko.ncjd.service.biz.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest(properties = "spring.profiles.active=local")
public class JwtTest {
    
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    

    @Test
    void createJWT() {
        User userDb = userService.lambdaQuery().eq(User::getId, "1694173827450880001").one();
        String token = jwtService.createToken(userDb);
        System.out.println(token);
    }

    @Test
    void createBCrypt() {
        String encode = passwordEncoder.encode("111111");
        System.out.println(encode);
    }

    @Test
    void ensureBCrypt() {
        // 在某个测试方法或控制器中运行
        String encode = passwordEncoder.encode("111111");
        System.out.println("=== "+encode+" ===");
        boolean matches = passwordEncoder.matches("111111", "$2a$10$2RCSWzWT3RLcU3RfORfgJe3dBr7yBuj70jV3LLkhp5desynKVeawq");
        System.out.println("密码匹配: " + matches);
    }
}
