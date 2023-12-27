package com.wr.service.imp;

import com.wr.constant.ConstantPool;
import com.wr.constant.UserConstants;
import com.wr.module.ResponseData;
import com.wr.module.user.LoginBody;
import com.wr.module.user.LoginUser;
import com.wr.module.user.SysUser;
import com.wr.redis.RedisCache;
import com.wr.service.LoginService;
import com.wr.service.UserService;
import com.wr.utils.SecurityUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author : WangRui
 * @date : 2023/12/6
 */

@Service
public class LoginServiceImpl implements LoginService {


    // 令牌自定义标识
    @Value("${token.header}")
    private String header;

    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;

    // 令牌有效期（默认30分钟）
    @Value("${token.expireTime}")
    private int expireTime;

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    private static final Long MILLIS_MINUTE_TEN = 300 * 60 * 1000L;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private UserService userService;

    @Override
    public ResponseData<Map<String, String>> login(LoginBody loginBody) {

        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginBody.getUserName(), loginBody.getPassWord()));
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("登陆失败");
        }

        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getSysUser().getUserId().toString();
        loginUser.setUserIdToken(userId);
        refreshToken(loginUser);
        Map<String, Object> claims = new HashMap<>();
        claims.put(ConstantPool.LOGIN_USER_KEY, userId);

        String token = createToken(claims);

        HashMap<String, String> tokenMap = new HashMap<>();
        tokenMap.put(ConstantPool.TOKEN, token);
        return new ResponseData<>(200, "成功", tokenMap);


    }

    @Override
    public ResponseData logout() {
        return null;
    }

    @Override
    public ResponseData<String> register(LoginBody loginBody) {

        String msg = "";
        String username = loginBody.getUserName();
        String password = loginBody.getPassWord();

        if (StringUtils.isEmpty(username)) {
            msg = "用户名不能为空";
        } else if (StringUtils.isEmpty(password)) {
            msg = "用户密码不能为空";
        } else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            msg = "账户长度必须在2到20个字符之间";
        } else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            msg = "密码长度必须在5到20个字符之间";
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(username))) {
            msg = "保存用户'" + username + "'失败，注册账号已存在";
        } else {
            SysUser sysUser = new SysUser();
            sysUser.setUserId(new SecureRandom().nextLong());
            sysUser.setUsername(username);
            sysUser.setPassword(SecurityUtils.encryptPassword(password));
            boolean flag = userService.save(sysUser);
            if (!flag) {
                msg = "注册失败,请联系系统管理人员";
            }
        }
        return msg.equals("") ? new ResponseData<>(200, "成功", "注册成功") :
                new ResponseData<>(400, "失败", msg);
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUser loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = ConstantPool.LOGIN_TOKEN_KEY + loginUser.getUserIdToken();
        redisCache.setCacheObject(userKey, loginUser, expireTime, TimeUnit.MINUTES);
    }


    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims) {
        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }
}
