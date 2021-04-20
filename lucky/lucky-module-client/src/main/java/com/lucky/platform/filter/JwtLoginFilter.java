package com.lucky.platform.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucky.platform.entity.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * security 自定义认证过滤器
 *
 * @author 53276
 */
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JwtLoginFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * 认证
     *
     * @param request  request
     * @param response response
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            //把request中流的数据转换为 User Bean
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            //
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
            return authenticationManager.authenticate(authRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 认证成功 给客户返回 Token
     *
     * @param request
     * @param response
     * @param chain
     * @param authResult
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        //认证成功后通过authResult得到用户的属性
        User user = new User();
        user.setUserName(authResult.getName());
        String token = "123456";
        //把 Token 写入到请求头
        response.setHeader("Authorization", "Bearer " + token);

    }
}
