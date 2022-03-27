package com.lucky.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lucky.platform.entity.city;
import java.util.List;
import java.util.Map;

/**
 * @author 53276
 */
public interface CityService extends IService<city> {

    List<Map<String, Object>> cityList();
}
