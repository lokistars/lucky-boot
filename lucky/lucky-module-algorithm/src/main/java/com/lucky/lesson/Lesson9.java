package com.lucky.lesson;

/**
 * 链表相关
 * 1、输入链表头节点,奇数长度返回中点,偶数长度返回上中点.
 * 2、输入链表头节点,奇数长度返回中点,偶数长度返回下中点.
 * 3、输入链表头节点,奇数长度返回中点前一个,偶数长度返回中点前一个.
 * 4、输入链表头节点,奇数长度返回中点后一个,偶数长度返回中点后一个.
 * 定义两个指针 a,b,a指针每次跳一次,b指针每次跳两次, b指针跳到尾a指针刚好在中点。
 * 判断一个链表是否回文
 * 1、通过栈,把所有数据写入栈中,栈弹出的值跟链表第一个节点对比是不是一样的
 * 2、通过两个指针a,b 获取到链表中点,把中点后面的链表指向反转,尾节点b指向上一个节点
 * @author: Loki
 * @data: 2021-11-14 18:37
 **/
public class Lesson9 {
    /**
     * 默认偶数,true 奇数
     */
    private static  boolean type;

    private static Node test1(Node head){
        if (head == null || head.next == null || head.next.next == null){
            return head;
        }
        Node next = head.next;
        Node douNext = head.next.next;
        while (douNext.next != null && douNext.next.next != null){
            next = next.next;
            douNext = douNext.next.next;
        }
        next.next = null;
        return next;
    }

    /**
     *  从第二个节点开始,每次循环跳两步骤
     */
    private static Node test2(Node head){
        if (head == null || head.next == null){
            return head;
        }
        Node next = head.next;
        Node douNext = head.next;
        while(douNext.next != null && douNext.next.next != null){
            next = next.next;
            douNext = douNext.next.next;
        }
        return head;
    }

    /**
     * 中点前一个
     */
    private static Node test3(Node head){
        if (head == null || head.next == null){
            return head;
        }
        Node next = head;
        Node douNext = head.next.next;
        while (douNext.next != null && douNext.next.next != null){
            next = next.next;
            douNext = douNext.next.next;
        }
        return next;
    }

    /**
     * 中点后一个
     */
    private static Node test4(Node head){
        if (head == null || head.next == null){
            return head;
        }
        Node next = head.next.next;
        Node douNext = head.next.next;
        while (douNext.next != null && douNext.next.next != null){
            next = next.next;
            douNext = douNext.next.next;
        }
        return next;
    }
    // 1:06
    private static void test5(Node head){

    }


    public static void main(String[] args) {
        Node node = new Node(0);
        node.next = new Node(1);
        node.next.next = new Node(2);
        node.next.next.next = new Node(3);
        node.next.next.next.next = new Node(4);
        //node.next.next.next.next.next = new Node(5);
        test4(node);
    }


    static class Node{
        Integer val ;
        Node next;
        public Node(Integer val) {
            this.val = val;
        }
    }
}
