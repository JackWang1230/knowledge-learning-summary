package cn.wr.designPattern.sample;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 手机号验证码登录实现类
 *
 * @author : WangRui
 * @date : 2023/2/28
 */

@Component
@LoginChannel(code = LoginChannelCode.VERIFCODE_LOGIN_CODE)
public class VerifCodeLogin extends LoginAdapter {

    @Override
    public Result login(String loginType, String userName, String password, String code) {
        //验证码登录方式一些参数验证逻辑
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(code)) {
            return Result.failed().msg("手机号或验证码不能为空！！！");
        }
        return Result.ok().msg("验证码方式登陆成功！！！");
    }

}
