package com.lucky.config.jdk;

import com.lucky.config.util.MyHashMap;

import java.util.Map;

/**
 * @author: Loki
 * @data: 2022-04-20 14:56
 */
public class Test {

    public static void main(String[] args) throws Exception {
        Map<String, String> map = new MyHashMap<>(8);
        map.put("a","2");
        map.put("Aa","2");
        map.put("BB","2");
        map.put("CC","2");
        map.put("DD","2");
        map.put("RR","2");
        map.put("TT","2");
        map.put("AA","2");
        map.put("AC","2");
        map.put("AB","2");
        map.remove("");
        System.out.println(map.get("a"));

        System.in.read();
    }
}
