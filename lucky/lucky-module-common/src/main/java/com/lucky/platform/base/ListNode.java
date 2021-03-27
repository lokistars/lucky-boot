package com.lucky.platform.base;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: lucky.module
 * @description: 链表 实现
 * @author: Loki
 * @create: 2021-03-27 13:20
 **/
public class ListNode {
  private int value ;
  private ListNode next;

  public ListNode(int val){
      this.value = val;
  }

  public ListNode(int val, ListNode nodeList){
    this.value = val;
    this.next = nodeList;
  }

  public static  void main(String[] args){
    int[] arr = {2,4,6,8,9};

    ListNode node = arrayToNode(arr);
    println(node);
  }

  public static ListNode arrayToNode(int[] arr){
    ListNode node = new ListNode(arr[0]);
    ListNode other = node;
    for (int i = 1; i < arr.length; i++) {
      ListNode tmp = new ListNode(arr[i]);
      other.next = tmp;
      other = tmp;
    }
    return node;
  }

  public static void println(ListNode node){
      while (node.next != null){
        System.out.print(node.value+",");
        node = node.next;
      }
      System.out.println(node.value);
  }

}
