package com.loogingko.ncjd.service.biz;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.loogingko.ncjd.mapper.UserMapper;
import com.loogingko.ncjd.model.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {
}
