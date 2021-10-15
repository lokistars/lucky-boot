package com.lucky.platform.controller;

import com.lucky.platform.ClientApplication;
import com.lucky.platform.service.CityService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

/**
 * @author: Loki
 * @data: 2021-10-15 13:39
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ClientApplication.class)
public class TestController {

    @Autowired
    private CityService cityService;

    @Test
    public void test1(){
        final List<Map<String, Object>> list = cityService.cityList();

        System.out.println(list);

    }
}
