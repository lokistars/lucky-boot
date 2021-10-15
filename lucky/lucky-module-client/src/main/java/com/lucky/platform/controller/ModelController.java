package com.lucky.platform.controller;

import com.alibaba.fastjson.JSONObject;
import com.lucky.platform.annotation.CheckSession;
import com.lucky.platform.service.CityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RefreshScope
@RequestMapping("/config")
@Api(value = "/config", description = "User 相关操作")
public class ModelController {

    @Autowired
    private RestTemplate restTemplate;


    private CityService cityService;

    @Autowired
    public void setCityService(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/cityInfo")
    @ApiOperation(value = "/cityInfo", notes = "测试")
    public List<Map<String, Object>> query() {
        List<Map<String, Object>> cities = cityService.cityList();
        return cities;
    }


    @PostMapping("/query")
    @ApiOperation(value = "/model", notes = "Model")
    public String ModelQuery(@RequestBody JSONObject params) {
        String applyId = params.getString("applyId");
        JSONObject postData = new JSONObject();
        postData.put("applyId", applyId);
        String url = "http://172.16.11.123:9011/page/initpdf";
        String body = restTemplate.postForEntity(url, postData, String.class).getBody();
        JSONObject object = JSONObject.parseObject(body);
        System.out.println(object);
        return body;
    }

    @PostMapping("/input")
    @CheckSession("sessionCode")
    public String input(String sessionCode) {
        try {
            System.out.println(sessionCode);
            TimeUnit.SECONDS.sleep(3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "成功";
    }

}
