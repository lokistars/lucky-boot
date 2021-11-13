package com.lucky.lesson;

import org.w3c.dom.Node;

/**
 * 基础的数据结构
 * @author: Loki
 * @data: 2021-11-13 13:37
 **/
public class Lesson3 {


    /**
     * 单链表反转
     * 存储反转后的数据 ,记下一个节点的位置。
     */
    private static Node reverseLinkedList(Node head){
        Node pre = null;
        Node next = null;
        while (head != null){
            next = head.next;
            head.next = pre;
            pre = head;
            head = next;
        }
        return pre;
    }

    /**
     * 双向链表反转
     */
    private static DoubleNode reverseDoubleList(DoubleNode head){
        DoubleNode pre = null;
        DoubleNode next = null;
        while (head != null){
            next = head.next;
            head.next = pre;
            head.last = next;
            pre = head;
            head = next;
        }
        return pre;
    }

    /**
     * 链表删除指定数,找出第一个头节点不相等的位置
     */
    private static Node delNode(Node head,int data){
        while (head !=null && head.val == data){
            head = head.next;
        }
        Node pre = head;
        while (pre.next != null){
            if (pre.next.val == data){
                pre.next = pre.next.next;
            }else{
                pre = pre.next;
            }
        }
        return head;
    }


    private static void stackToQueue(){

    }

    public static void main(String[] args) {

    }





    static class Node{
        int val;
        Node next;
        public Node(int data){
            val =data;
        }
    }

    class DoubleNode {
        int val;
        DoubleNode next;
        DoubleNode last;
        public DoubleNode(int data){
            val =data;
        }
    }
}
