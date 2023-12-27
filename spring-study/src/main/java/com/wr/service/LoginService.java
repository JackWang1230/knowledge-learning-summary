package com.wr.service;

import com.wr.module.ResponseData;
import com.wr.module.user.LoginBody;

import java.util.Map;

public interface LoginService {

    ResponseData<Map<String,String>> login(LoginBody loginBody);

    ResponseData<String> logout();

    ResponseData<String> register(LoginBody loginBody);



}
