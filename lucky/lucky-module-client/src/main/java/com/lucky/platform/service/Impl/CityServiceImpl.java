package com.lucky.platform.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lucky.platform.entity.AreasTown;
import com.lucky.platform.entity.User;
import com.lucky.platform.entity.city;
import com.lucky.platform.mapper.AreasTownMapper;
import com.lucky.platform.mapper.CityMapper;
import com.lucky.platform.service.CityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 53276
 */
@Service
public class CityServiceImpl implements CityService {
    private static final Logger log = LoggerFactory.getLogger(CityServiceImpl.class);

    @Autowired
    private CityMapper cityMapper;

    @Autowired
    private AreasTownMapper mapper;


    @Override
    public List<Map<String, Object>> cityList() {
        List<city> cities = cityMapper.selectList(null);
        QueryWrapper<AreasTown> wrapper = new QueryWrapper<>();
        mapper.delete(wrapper);
        Integer st = null;
        Integer shi = null;
        for (int i = 0; i < cities.size(); i++) {
            String s = cities.get(i).getId().toString();
            if ("0000".equals(s.substring(s.length() - 4, s.length()))) {
                AreasTown town = new AreasTown();
                town.setAreaId(cities.get(i).getId());
                town.setName(cities.get(i).getName());
                town.setParentId(0);
                town.setType(1);
                mapper.insert(town);
                log.info(cities.get(i).getName() + "省");
                st = cities.get(i).getId();
                shi = null;
            }
            if ("00".equals(s.substring(s.length() - 2, s.length())) && !"0000".equals(s.substring(s.length() - 4, s.length()))) {
                AreasTown town = new AreasTown();
                town.setAreaId(cities.get(i).getId());
                town.setName(cities.get(i).getName());
                town.setParentId(st);
                town.setType(2);
                mapper.insert(town);
                log.warn(cities.get(i).getName() + "市");
                shi = cities.get(i).getId();
            }
            if (!"00".equals(s.substring(s.length() - 2, s.length()))) {
                AreasTown town = new AreasTown();
                town.setAreaId(cities.get(i).getId());
                town.setName(cities.get(i).getName());
                if (shi == null) {
                    town.setParentId(st);
                    town.setType(2);
                } else {
                    town.setParentId(shi);
                    town.setType(3);
                }
                mapper.insert(town);
                log.error(cities.get(i).getName() + "区");
            }
        }
        List<Map<String, Object>> list1 = new ArrayList<>();
        return list1;
    }
}
