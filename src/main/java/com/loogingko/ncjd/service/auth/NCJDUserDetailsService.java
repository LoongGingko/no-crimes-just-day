package com.loogingko.ncjd.service.auth;

import com.loogingko.ncjd.model.entity.User;
import com.loogingko.ncjd.service.biz.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * SpringSecurity 登录校验实现
 * @author LiuRunYu 2026-04-10
 */
@Service
public class NCJDUserDetailsService implements UserDetailsService {

    private final UserService userService;
    private final JwtService jwtService;

    public NCJDUserDetailsService(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 检查用户是否存在
        User userDb = userService.lambdaQuery().eq(User::getUsername, username).one();
        if (userDb == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        // 2. 返回用户对象，交由SpringSecurity处理
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(userDb.getUsername())
                .password(userDb.getPassword())
//                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
        return userDetails;
    }
}
