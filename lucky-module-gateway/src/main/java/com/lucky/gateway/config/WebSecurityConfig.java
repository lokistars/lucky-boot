/*
package com.lucky.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

*/
/**
 * @author Nuany
 *//*

@EnableWebSecurity
@Configuration
//@Profile("ignoring")
public class WebSecurityConfig */
/*extends WebSecurityConfigurerAdapter*//*
 {

    */
/**
     * 加密方式注入到IOC容器
     *
     * @return
     *//*

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    */
/**
     * 配置user-detail服务
     *
     * @param auth 认证管理器
     * @throws Exception
     *//*

    //@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //获取userDetails对象
        auth.userDetailsService(new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return new User("username","password",null);
            }
        }).passwordEncoder(passwordEncoder());
    }

    */
/**
     * 全局请求忽略规则配置，一般用来配置无需安全检查的路径 （比如说静态文件，比如说注册页面）
     *
     * @param web Web
     * @throws Exception
     *//*

    */
/*@Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }*//*


    */
/**
     * Request层面的配置
     *
     * @param http http 权限控制
     * @throws Exception
     *//*

    */
/*@Override
    protected void configure(HttpSecurity http) throws Exception {
        //csrf 过滤器默认"GET","HEAD","TRACE","OPTIONS" 不拦截 其他请求如 post 会被拦截, csrf 默认启动,  disable 进行释放
        http.csrf().disable().authorizeRequests()
                //释放资源, 可以直接访问 permitAll表示释放
                .antMatchers("/**").permitAll();
    }*//*



    */
/**
     * WebSecurityConfigurerAdapter 被禁用HttpSecurity的解决方案
     * <a href="https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter#ldap-authentication">...</a>
     * @param http
     * @return
     * @throws Exception
     *//*

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests(auth-> auth.antMatchers("/doc.html").permitAll());
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web-> web.ignoring().antMatchers("/doc.html");
    }


}
*/
