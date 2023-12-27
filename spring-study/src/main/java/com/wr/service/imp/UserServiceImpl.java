package com.wr.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wr.constant.UserConstants;
import com.wr.mapper.UserMapper;
import com.wr.module.user.SysUser;
import com.wr.service.UserService;
import org.springframework.stereotype.Service;


/**
 * @author : WangRui
 * @date : 2023/12/5
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, SysUser> implements UserService {

    @Override
    public SysUser getUsersByUsername(String username) {
        LambdaQueryWrapper<SysUser> qw = new QueryWrapper<SysUser>()
                .lambda()
                .eq(SysUser::getUsername, username);
        return this.getBaseMapper().selectOne(qw);
    }

    @Override
    public String checkUserNameUnique(String userName) {

        LambdaQueryWrapper<SysUser> qw = new QueryWrapper<SysUser>()
                .lambda()
                .eq(SysUser::getUsername, userName);
        Integer count = this.getBaseMapper().selectCount(qw);
        if (count > 0) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;

    }
}
