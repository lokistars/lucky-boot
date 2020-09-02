package com.lucky.platform.service;

import com.lucky.platform.entity.city;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author 53276
 */
public interface CityService {

    public List<Map<String, Object>> cityList();
}
