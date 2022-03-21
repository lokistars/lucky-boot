package com.lucky.lesson;

import java.util.Arrays;
import java.util.Comparator;

/**
 * 贪心算法
 *
 * @author: Loki
 * @data: 2021-11-29 09:14
 **/
public class Lesson13 {

    /**
     * 给定一颗二叉树的头节点head,返回这棵二叉树中最大的二叉搜索子数的头节点
     * 练习
     */

    /**
     * 给定一颗二叉树的头节点head,和另外两个节点a和b,返回a和b的最低公共祖先
     * 最低公共祖先定义:
     */

    /**
     * 给定一个由字符串数组,必须把所有字符串拼接起来,返回所有可能拼接结果中,字典序最小的结果
     * 字典序: 字符串排序比大小,如果字符串长度不一样,短的那个后面补充ascii码0
     *
     */
    static class MyComparator implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            return (a+b).compareTo(b+a);
        }
    }
    private static String lowestString(String[] strs){
        if (strs == null || strs.length ==0){
            return "";
        }
        Arrays.sort(strs,new MyComparator());
        StringBuilder sbu = new StringBuilder();
        for (String str : strs) {
            sbu.append(str);
        }
        return sbu.toString();
    }
}
