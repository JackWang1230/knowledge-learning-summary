package com.wr.mapper;


import com.wr.module.user.SysUser;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface UserMapper extends BaseMapper<SysUser> {

}
