package com.lucky.config.jdk;

import com.lucky.config.service.UserService;

import java.io.*;
import java.util.List;

/**
 * @author: Loki
 * @data: 2021-12-03 23:31
 **/
public class Jdk9 {


    private static void person() throws Exception{
        // 普通类支持匿名内部类，只针对当前对象
        UserService userService = new UserService(){
            @Override
            public boolean equals(Object obj) {
                return super.equals(obj);
            }
        };
        // try语法升级
        InputStream inp = new FileInputStream(new File(""));
        OutputStream out = new FileOutputStream("");
        try (inp;out){
            inp.transferTo(out);
        }catch (Exception e){
            throw new RuntimeException();
        }
        // String  底层存储结构，8是用 char[] 数组存储 , 9是用的byte[] 数组, char 存储2个字节 byte 1字节
        // 创建只读集合
        List<String> list = List.of("321","123");
        // 模块化管理 创建module-info.java 文件

    }
}

/**
 * 接口中可以定义私有方法，成员变量不可以私有
 */
interface TestService{
    static void test1(){

    }
    default void test2(){
        test();
    }

    private  void test(){

    }
}