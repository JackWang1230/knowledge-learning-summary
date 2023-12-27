package com.wr.security.filter;

import com.wr.constant.ConstantPool;
import com.wr.module.user.LoginUser;
import com.wr.redis.RedisCache;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author : WangRui
 * @date : 2023/12/6
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {


    @Autowired
    private RedisCache redisCache;

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

    /**
     *  这就是一个token 拦截器 用来拦截每次带有token的请求进行解析查询用户信息
     *  封装好后给SecurityContextHolder管理
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader(this.header);
        if (StringUtils.isNotBlank(token) && token.startsWith(ConstantPool.TOKEN_PREFIX)){
            token = token.replace(ConstantPool.TOKEN_PREFIX,"");
        }
        LoginUser user = null;
        if (StringUtils.isNotBlank(token)){

            try {
                Claims claims = parseToken(token);
                String uuid = (String)claims.get(ConstantPool.LOGIN_USER_KEY);

                String userKey = getTokenKey(uuid);
                user = redisCache.getCacheObject(userKey);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        if (!Objects.isNull(user) && Objects.isNull(getAuthentication())){

            verifyToken(user);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, null);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // 放行
        filterChain.doFilter(request, response);


    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token)
    {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    private String getTokenKey(String uuid)
    {
        return ConstantPool.LOGIN_TOKEN_KEY + uuid;
    }


    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     *
     * @param loginUser
     * @return 令牌
     */
    public void verifyToken(LoginUser loginUser)
    {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN)
        {
            refreshToken(loginUser);
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUser loginUser)
    {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getUserIdToken());
        redisCache.setCacheObject(userKey, loginUser, expireTime, TimeUnit.MINUTES);
    }

    /**
     * 获取Authentication
     */
    public Authentication getAuthentication()
    {
        return SecurityContextHolder.getContext().getAuthentication();
    }


}
