package cn.wr.designPattern.sample;


/**
 * 登录接口
 */
public interface LoginService {

    Result login(String loginType, String userName, String password, String code);
}
