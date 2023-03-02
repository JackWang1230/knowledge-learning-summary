package cn.wr.designPattern.sample;

/**
 * 登录方式code常量类
 *
 * @author : WangRui
 * @date : 2023/2/28
 */

public final class LoginChannelCode {

    private LoginChannelCode() {
        throw new AssertionError("不能产生实例");
    }

    /**
     * 密码方式 登录
     */
    public static final String PASSWORD_LOGIN_CODE = "password";

    /**
     * 验证码 登录
     */
    public static final String VERIFCODE_LOGIN_CODE = "code";

    /**
     * 一键登录
     */
    public static final String ONEKEY_LOGIN_CODE = "onekey";

}
