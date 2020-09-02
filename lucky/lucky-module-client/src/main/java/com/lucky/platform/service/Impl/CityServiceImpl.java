package com.lucky.platform.service.Impl;

import com.lucky.platform.entity.city;
import com.lucky.platform.mapper.CityMapper;
import com.lucky.platform.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 53276
 */
@Service
public class CityServiceImpl implements CityService {
    private CityMapper cityMapper;
    @Autowired
    public void setCityMapper(CityMapper cityMapper) {
        this.cityMapper = cityMapper;
    }

    @Override
    public List<Map<String, Object>> cityList() {
        List<city> cities = cityMapper.selectList(null);
        List<String> list = list = new ArrayList<>();
        String sen = "";
        String shi = "";
        String see = "";
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> map1 = null;
        List<Map<String, Object>> list1 = new ArrayList<>();
        for (int i = 0; i < cities.size(); i++) {
            String str = cities.get(i).getId() + "";
            String sub = str.substring(str.length()-4, str.length());
            if (!"".equals(sen)&&!"0000".equals(sub)){
                if ("00".equals(sub.substring(sub.length()-2,sub.length()))){
                    see = cities.get(i).getName().trim();
                }
                if ("".equals(see)||("00".equals(sub.substring(sub.length()-2,sub.length()))&&list.size()>0)){
                        if (list.size()>0){
                            map1.put(see,list);
                        }else{
                            map1.put(cities.get(i).getName().trim(),list);
                        }
                        list = new ArrayList<>();
                        see = "";
                 }else if (!"".equals(see)&&!"00".equals(sub.substring(sub.length()-2,sub.length()))){
                        list.add(cities.get(i).getName().trim());
                }
            }
            if ("0000".equals(sub)){
                if (map1!= null&& map1.size()>0){
                    map.put(shi,map1);
                }
                shi = cities.get(i).getName().trim();
                sen = str.substring(str.length()-2, str.length());
                map1 = new HashMap<>();
            }
        }
        list1.add(map);
        return list1;
    }
}
