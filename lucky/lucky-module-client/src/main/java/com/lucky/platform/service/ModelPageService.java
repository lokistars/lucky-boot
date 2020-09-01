package com.lucky.platform.service;

import com.lucky.platform.entity.ModelPage;
import com.lucky.platform.mapper.ModelPageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Nuany
 */
@Component
public class ModelPageService {
    @Autowired
    ModelPageMapper modelPageMapper;

    public List<ModelPage> findAll(){

        return modelPageMapper.selectList(null);
    };


    public Map<String,Object> byExample(){
        Map<String,Object> map = new HashMap<String,Object>();
        ModelPage page = modelPageMapper.selectById("15530");
        map.put("page",page);
        return map;
    }
}
