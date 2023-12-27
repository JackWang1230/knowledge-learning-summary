package com.wr.module.user;

/**
 * @author : WangRui
 * @date : 2023/12/5
 */

public class LoginBody {

    private String username;
    private String password;

    public String getUserName() {
        return username;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public String getPassWord() {
        return password;
    }

    public void setPassWord(String passWord) {
        this.password = passWord;
    }
}
