package com.lucky.lesson;

/**
 * 链表相关
 * 1、输入链表头节点,奇数长度返回中点,偶数长度返回上中点.
 * 2、输入链表头节点,奇数长度返回中点,偶数长度返回下中点.
 * 3、输入链表头节点,奇数长度返回中点前一个,偶数长度返回中点前一个.
 * 4、输入链表头节点,奇数长度返回中点后一个,偶数长度返回中点后一个.
 * @author: Loki
 * @data: 2021-11-14 18:37
 **/
public class Lesson9 {
    /**
     * 默认偶数,true 奇数
     */
    private static  boolean type;

    private Node test(Node head){

        return head;
    }

    public static void main(String[] args) {
        System.out.println(type);
    }
    class Node{
        Integer val ;
        Node next;
        public Node(Integer val, Node next) {
            this.val = val;
            this.next = next;
        }
    }
}
