package com.lucky.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lucky.platform.entity.User;
import com.lucky.platform.mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * <p>
 * 用户登录 服务类
 * </p>
 *
 * @author Nuany
 * @since 2020-09-12
 */

public interface UserService extends IService<User>, UserDetailsService {

}
