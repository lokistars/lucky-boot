package com.lucky.platform.demo;


import java.util.*;

/**
 * @program: lucky
 * @description: Map 存储的是Key - value 键值映射的格式
 * @author: Loki
 * @create: 2020-11-05 15:09
 **/
public class MapDemo {
    /**
     *  jdk 1.7 数据结构 数组+ 链表   jdk 1.8 数组+链表 +红黑树
     *
     * @param args
     */
    public static void main(String[] args) {
        shu();
    }


    public static void shu(){
        // 向左移四位 移动一位数是 * 2  hashMap中 默认是 2的N次幂
        int default_int = 1<<4;
        Map<String,String> map = new HashMap<>();
        System.out.println(default_int);
    }


    public void MapPut(){
        Map<String,String> map = new HashMap<>();
        map.put("小一","1");
        map.put("小二","2");
        map.put("小三","3");
        map.put("小四","4");
        System.out.println("判断是否为空:"+map.isEmpty());
        System.out.println(map.containsKey("小一")+""+map.containsValue("3"));
        for(Map.Entry<String, String> entry : map.entrySet()){
            System.out.println("key= "+entry.getKey()+" and value= "+entry.getValue());
        }
        //只能获取value的值,不能获取key
        Collection<String> values = map.values();
        for (String value : values) {
            System.out.println(value);
        }
        Set<String> strings = map.keySet();
        Iterator<String> iterator = strings.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }
}
