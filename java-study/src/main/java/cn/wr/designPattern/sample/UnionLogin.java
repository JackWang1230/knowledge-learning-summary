package cn.wr.designPattern.sample;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author : WangRui
 * @date : 2023/2/28
 */

public class UnionLogin {


    @Autowired
    private LoginService loginService;

    @ApiOperation(value = "登录", notes = "用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名(手机号)", required = true, paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "code", value = "验证码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "loginType", value = "登录方式", defaultValue = "password:密码  code:验证码 onekey:手机号一键登陆", required = true, paramType = "query")
    })
    @PostMapping("/login")
    public Result login(@RequestParam(value = "username") String username,
                        @RequestParam(value = "password") String password,
                        @RequestParam(value = "code") String code,
                        @RequestParam(value = "loginType") String loginType) {
//        if(loginType == 1){
//            System.out.println("账号密码方式登录！！！");
//        }else if(loginType == 2){
//            System.out.println("手机号验证码方式登录！！！");
//        }if(loginType == 3){
//            System.out.println("手机号一键登录！！！");
//        }else {
//            System.out.println("登录方式错误！！！");
//        }
//        return Result.ok();

        /** 改进后 **/
        Result result = loginService.login(loginType, username, password, code);
        return result;
    }
}
