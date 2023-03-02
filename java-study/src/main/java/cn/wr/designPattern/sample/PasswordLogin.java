package cn.wr.designPattern.sample;


import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 账号密码登录实现类
 *
 * @author : WangRui
 * @date : 2023/2/28
 */

@Component
@LoginChannel(code = LoginChannelCode.PASSWORD_LOGIN_CODE)
public class PasswordLogin extends LoginAdapter {

    @Override
    public Result login(String loginType, String userName, String password, String code) {
        //用户名登录方式一些参数验证逻辑
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            return Result.failed().msg("用户名密码不能为空！！！");
        }
        if (!"admin".equals(userName) || !"111111".equals(password)) {
            return Result.failed().msg("用户名或密码错误！！！");
        }
        return Result.ok().msg("密码方式登陆成功！！！");
    }

}
