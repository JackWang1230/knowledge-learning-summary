package cn.wr.designPattern.sample;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 手机号一键登录实现类
 *
 * @author : WangRui
 * @date : 2023/2/28
 */


@Component
@LoginChannel(code = LoginChannelCode.ONEKEY_LOGIN_CODE)
public class OnkeyLogin extends LoginAdapter {
    @Override
    public Result login(String loginType, String userName, String password, String code) {
        //一键登录方式一些参数验证逻辑
        if (StringUtils.isEmpty(userName)) {
            return Result.failed().msg("手机号不能为空！！！");
        }
        return Result.ok().msg("一键登录方式登陆成功！！！");
    }
}
