package com.lucky.nacos.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.lucky.nacos.annotation.CheckSesson;
import com.lucky.nacos.entity.ModelPage;
import com.lucky.nacos.service.ModelPageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.*;
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

    @Autowired
    private ModelPageService modelPageService;

    @GetMapping("/configinfo")
    @ApiOperation(value = "/configinfo", notes = "测试")
    public String query() {
        return "fjkdla";
    }

    @PostMapping("/model")
    @ApiOperation(value = "/model", notes = "Model")
    public Map ModelPage() {
        Map map = new HashMap();
        List<ModelPage> all = modelPageService.findAll();
        map.put("page", all);
        return map;
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
    public String input(@CheckSesson("sessionCode") String sessionCode) {
        try {
            System.out.println(sessionCode);
            TimeUnit.SECONDS.sleep(3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "成功";
    }

}
