package cn.wr.designPattern.sample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * 登录方式 代理类
 *
 * @author : WangRui
 * @date : 2023/2/28
 */
@Slf4j
public class LoginDelegate extends LoginAdapter {
    @Override
    public Result login(String loginType, String userName, String password, String code) {
        // 返回实际的类型
        LoginService loginService = getLoginService(loginType);
        if (ObjectUtils.isEmpty(loginService)) {
            return Result.failed().msg("登录方式错误！！！");
        }
        return loginService.login(loginType, userName, password, code);
    }


    private Map<String, LoginService> map;

    public LoginDelegate(Map<String, LoginService> map) {
        log.info("初始化登录方式====>{}种--{}", map.size(), map);
        this.map = map;
    }


    /**
     * @param loginType 登录方式
     * @return
     */
    private LoginService getLoginService(String loginType) {
        if (map.containsKey(loginType)) {
            return map.get(loginType);
        }
        return null;
    }

}
