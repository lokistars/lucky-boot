//将两个升序链表合并为一个新的 升序 链表并返回。新链表是通过拼接给定的两个链表的所有节点组成的。 
//
// 
//
// 示例 1： 
//
// 
//输入：l1 = [1,2,4], l2 = [1,3,4]
//输出：[1,1,2,3,4,4]
// 
//
// 示例 2： 
//
// 
//输入：l1 = [], l2 = []
//输出：[]
// 
//
// 示例 3： 
//
// 
//输入：l1 = [], l2 = [0]
//输出：[0]
// 
//
// 
//
// 提示： 
//
// 
// 两个链表的节点数目范围是 [0, 50] 
// -100 <= Node.val <= 100 
// l1 和 l2 均按 非递减顺序 排列 
// 
// Related Topics 递归 链表 
// 👍 1616 👎 0

package com.lucky.leetCode.editor.cn;
//Java: 合并两个有序链表
public class P21MergeTwoSortedLists {
  public static void main(String[] args) {
    /*int[] arr = {6, 1, 8, 3};
    for (int i = 1; i < arr.length; i++) {
      for (int j = i - 1; j >= 0 && arr[j] > arr[j + 1]; j--) {
        swap(arr, j, j + 1);
      }
    }

    ListNode node = node(arr);
    System.out.println(node);*/
    int count = 0;
    int length = 10000;
    int arr[] = new int[8];
    for (int i = 0; i < 20; i++) {
      System.out.print(f2()+",");
    }



    P21MergeTwoSortedLists p21 = new P21MergeTwoSortedLists();
    Solution solution = p21.new Solution();
    ListNode node = new ListNode(1, new ListNode(3));
    ListNode node1 = new ListNode(2, new ListNode(4));
    ListNode listNode = solution.mergeTwoLists(node, node1);
    System.out.println(listNode);
  }
  public static int f(){
    return (int) (Math.random()*5)+1;
  }

  public static int f1(){
    int ans = 0;
    do {
      ans = f();
    }while (ans == 3);
      return ans>3?0:1;
  }
  public static int f2(){
    return (f1()<<2)+(f1()<<1)+f1();
  }

  public static ListNode node(int[] arr) {

    ListNode node = new ListNode(arr[0], new ListNode(arr[1]));

    return node;
  }

  public static void swap(int[] arr, int i, int j) {
    int tmp = arr[i];
    arr[i] = arr[j];
    arr[j] = tmp;
  }

  public static class ListNode {
    int val;
    ListNode next;

    ListNode() {
    }

    ListNode(int val) {
      this.val = val;
    }

    ListNode(int val, ListNode next) {
      this.val = val;
      this.next = next;
    }
  }

  class Solution {
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
      if (l1 == null) {
        return l2;
      }
      if (l2 == null) {
        return l1;
      }
      if (l1.val < l2.val) {
        l1.next = mergeTwoLists(l1.next, l2);
        return l1;
      } else {
        l2.next = mergeTwoLists(l1, l2.next);
        return l2;
      }
    }
  }
}

