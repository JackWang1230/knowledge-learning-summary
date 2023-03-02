package cn.wr.designPattern.sample;

/**
 * 登录接口适配器
 *
 * @author : WangRui
 * @date : 2023/2/28
 */

public abstract class LoginAdapter implements LoginService {

    @Override
    public Result login(String loginType, String userName, String password, String code) {
        return new Result();
    }
}
