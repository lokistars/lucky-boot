package com.lucky.platform.filter;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * security 自定义验证登录过滤器
 *
 * @author 53276
 */
public class JwtVerifyFilter extends BasicAuthenticationFilter {

    public JwtVerifyFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader("Authorization");
        // 判断token 是否为空
        if (header == null || header.startsWith("Bearer ")) {
            chain.doFilter(request, response);

        } else {
            String token = header.replace("Bearer ", "");
            //把用户对象放入容器中
            //SecurityContextHolder.getContext().setAuthentication();
            //后续处理器
            chain.doFilter(request, response);
        }
    }
}
