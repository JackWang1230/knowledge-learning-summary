package cn.wr.designPattern.sample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * 登录方式 config配置类 （此处是关键）
 *
 * @author : WangRui
 * @date : 2023/2/28
 */


@Configuration
public class LoginChannelConfiguration {

    @Bean
    @Primary
    public LoginFactoryBean repayFactoryBean(List<LoginService> list) {
        LoginFactoryBean factoryBean = new LoginFactoryBean();
        factoryBean.setLoginServiceList(list);
        return factoryBean;
    }
}
