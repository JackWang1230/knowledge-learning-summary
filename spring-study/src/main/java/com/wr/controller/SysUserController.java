package com.wr.controller;

import com.wr.module.ResponseData;
import com.wr.module.user.LoginBody;
import com.wr.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author : WangRui
 * @date : 2023/12/5
 */

@Api("用户信息")
@RestController
public class SysUserController {


    @Autowired
    private LoginService loginService;

    @ApiOperation("用户登陆")
    @PostMapping("/login")
    public ResponseData<Map<String, String>> login(@RequestBody LoginBody loginBody){

        return  loginService.login(loginBody);

    }

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public ResponseData<String> register(@RequestBody LoginBody loginBody ){

        return loginService.register(loginBody);
    }

}
