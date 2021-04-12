package com.lucky.algorithm.base;

import java.util.ArrayList;

/**
 * @program: lucky.module
 * @description: 链表 实现
 * @author: Loki
 * @create: 2021-03-27 13:20
 **/
public class ListNode {
  private int val;
  private ListNode next;

  public ListNode(int val) {
    this.val = val;
  }

  public ListNode(int val, ListNode nodeList) {
    this.val = val;
    this.next = nodeList;
  }

  public static void main(String[] args) {
    int[] arr = {2, 4, 6, 8};
    ListNode node = arrayToNode(arr);
    reversePrint(node);
    println(node);
    node = reversalNode(node);
    println(node);

  }

  /**
   * 数组转换为链表
   *
   * @param arr
   * @return
   */
  public static ListNode arrayToNode(int[] arr) {
    ListNode node = new ListNode(arr[0]);
    ListNode other = node;
    for (int i = 1; i < arr.length; i++) {
      ListNode tmp = new ListNode(arr[i]);
      // 将other的下一节点指向新生成的节点
      other.next = tmp;
      // 将other指向下一个节点
      other = tmp;
    }
    return node;
  }

  /**
   * 链表反转
   *
   * @param head
   * @return
   */
  public static ListNode reversalNode(ListNode head) {
    // 存储反转后的数据
    ListNode pre = null;
    // 记一下 下一个节点的位子
    ListNode next = null;
    while (head != null) {
      next = head.next;
      head.next = pre;
      pre = head;
      head = next;
    }
    return pre;
  }

  /**
   * 返回反转后的值
   *
   * @param head
   * @return
   */
  public static int[] reversePrint(ListNode head) {
    ArrayList<Integer> link = new ArrayList<>();
    while (head != null) {
      link.add(head.val);
      head = head.next;
    }
    int[] arr = new int[link.size()];
    for (int j = 0; j < arr.length; j++) {
      arr[j] = link.get(arr.length-j-1);
    }
    return arr;
  }

  public static void println(ListNode head) {
    while (head.next != null) {
      System.out.print(head.val + ",");
      head = head.next;
    }
    System.out.println(head.val);
  }
}
