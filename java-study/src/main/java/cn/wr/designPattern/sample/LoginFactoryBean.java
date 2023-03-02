package cn.wr.designPattern.sample;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录方式 工厂类
 *
 * @author : WangRui
 * @date : 2023/2/28
 */

public class LoginFactoryBean implements FactoryBean<LoginService>, InitializingBean {


    private LoginService loginService;

    @NonNull
    private List<LoginService> loginServiceList;

    public List<LoginService> getLoginServiceList() {
        return loginServiceList;
    }

    public void setLoginServiceList(@NonNull List<LoginService> loginServiceList) {
        this.loginServiceList = loginServiceList;
    }


    @Override
    public LoginService getObject() throws Exception {
        return loginService;
    }

    @Override
    public Class<?> getObjectType() {
        return loginService.getClass();
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        // 初始化过程 通过反射 拿到所有注解包含LoginChannel的类
        Map<String, LoginService> map = new HashMap<>(loginServiceList.size() << 1);
        for (LoginService r : loginServiceList) {
            LoginChannel channel = r.getClass().getAnnotation(LoginChannel.class);
            if (channel == null) {
                continue;
            }
            map.put(channel.code(), r);
        }

        // 将所有登录方式存入代理类中
        loginService = new LoginDelegate(map);
    }
}
