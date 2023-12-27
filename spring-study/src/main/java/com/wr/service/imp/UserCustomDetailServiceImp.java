package com.wr.service.imp;


import com.wr.module.user.LoginUser;
import com.wr.module.user.SysUser;
import com.wr.service.UserCustomDetailService;
import com.wr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * @author : WangRui
 * @date : 2023/12/5
 */
@Service
public class UserCustomDetailServiceImp implements UserCustomDetailService {

    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        SysUser usersByUsername = userService.getUsersByUsername(username);
        if (Objects.isNull(usersByUsername)){
            throw new RuntimeException("用户名或密码错误");
        }

        return new LoginUser(usersByUsername);
    }
}
