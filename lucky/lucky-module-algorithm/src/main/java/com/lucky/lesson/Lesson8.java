package com.lucky.lesson;

import java.util.HashMap;

/**
 * 前缀树
 * 从头节点出发,建出连续的节点位置,如果存在沿用之前的,每个节点有两个属性,
 * pass：记录有多少数走过这个节点
 * end: 有多少数在这个节点停止
 * 假设只有小写字母,给出默认的数组 next[26], 有26个字母,表达有多少跳路可以走
 * @author: Loki
 * @data: 2021-11-14 13:06
 **/
public class Lesson8 {
    private Node root;

    public Lesson8 (){
        this.root = new Node();
    }

    public static void main(String[] args) {
        Lesson8 less = new Lesson8();
        less.insert("a");
        less.prefixNumber("a");
        randomStrTest();
    }

    private void insert(String str){
        if (str == null){
            return ;
        }
        final char[] chars = str.toCharArray();
        Node node = this.root;
        node.pass++;
        int path = 0;
        //循环每个数,条件所有的数都是小写字母,减去a可以得到每个数的下标位置
        for (int i = 0; i < chars.length; i++) {
            path = chars[i] - 'a';
            if (node.next[path] == null){
                node.next[path] = new Node();
            }
            node = node.next[path];
            node.pass++;
        }
        node.end++;
    }
    // 查找是否存在,返回存在的位置
    private int search(String str){
        if (str == null){
            return 0;
        }
        final char[] chars = str.toCharArray();
        Node node = this.root;
        node.pass++;
        int path = 0;
        //循环每个数,条件所有的数都是小写字母,减去a可以得到每个数的下标位置
        for (int i = 0; i < chars.length; i++) {
            path = chars[i] - 'a';
            if (node.next[path] == null){
                return 0;
            }
            node = node.next[path];
        }
        return node.end;
    }

    /**
     * 根据前缀查找,有多少个是这个pre开头的
     */
    private int prefixNumber(String pre){
        if (pre == null){
            return 0;
        }
        final char[] chars = pre.toCharArray();
        Node node = this.root;
        node.pass++;
        int path = 0;
        for (int i = 0; i < chars.length; i++) {
            path = chars[i] - 'a';
            if (node.next[path] == null){
                return 0;
            }
            node = node.next[path];
        }
        return node.pass;
    }

    /**
     * 删除,如果下一个数的pass为 0,表示后面都没有节点了,下一个数= null
     */
    private void delete(String str){
        if (search(str) != 0){
            final char[] chars = str.toCharArray();
            Node node = this.root;
            node.pass --;
            int path = 0;
            for (int i = 0; i < chars.length; i++) {
                path = chars[i] - 'a';
                if (--node.next[path].pass == 0){
                    node.next[path] = null;
                    return ;
                }
                node = node.next[path];
            }
            node.end--;
        }
    }

    private static String randomStr(int len){
        char[] ans = new char[(int)(Math.random() * len)+1];
        for (int i = 0; i < ans.length; i++) {
            ans[i] = (char) (97 +(int) (Math.random() * 25));
        }
        return String.valueOf(ans);
    }

    private static void randomStrTest(){
        int strLen = 5;
        int testTimes = 1000;
        Lesson8 less = new Lesson8();
        Right right = new Right();
        for (int i = 0; i < testTimes; i++) {
            String str = randomStr(strLen);
            double decide = Math.random();
            if (decide < 0.25){
                less.insert(str);
                right.insert(str);
            }else if (decide < 0.50){
                if (less.prefixNumber(str) != right.prefixNumber(str)){
                    System.out.println("出错了：" +str);
                }
            }else if (decide < 0.75){
                if (less.search(str) != right.search(str)){
                    System.out.println("出错了：" +str);
                }
            }else{
                less.delete(str);
                right.delete(str);
            }
        }
        System.out.println("执行完毕");

    }

    /**
     * next[i] == null , 判断i方向的路不存在。
     */
    private static class Node{
        private int pass;
        private int end;
        private Node[] next;

        public Node (){
            this.pass = 0;
            this.end = 0;
            this.next = new Node[26];
        }
        public Node(int len) {
            this.pass = 0;
            this.end = 0;
            this.next = new Node[len];
        }
    }

    public static class Right {

        private HashMap<String, Integer> box;

        public Right() {
            box = new HashMap<>();
        }

        public void insert(String word) {
            if (!box.containsKey(word)) {
                box.put(word, 1);
            } else {
                box.put(word, box.get(word) + 1);
            }
        }

        public void delete(String word) {
            if (box.containsKey(word)) {
                if (box.get(word) == 1) {
                    box.remove(word);
                } else {
                    box.put(word, box.get(word) - 1);
                }
            }
        }

        public int search(String word) {
            if (!box.containsKey(word)) {
                return 0;
            } else {
                return box.get(word);
            }
        }

        public int prefixNumber(String pre) {
            int count = 0;
            for (String cur : box.keySet()) {
                if (cur.startsWith(pre)) {
                    count += box.get(cur);
                }
            }
            return count;
        }
    }
}
