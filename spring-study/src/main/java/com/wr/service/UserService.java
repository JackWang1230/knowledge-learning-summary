package com.wr.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wr.module.user.SysUser;



public interface UserService extends IService<SysUser> {

    SysUser getUsersByUsername(String username);

    String checkUserNameUnique(String userName);
}
