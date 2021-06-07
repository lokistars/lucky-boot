//给你一个链表的头节点 head 和一个整数 val ，请你删除链表中所有满足 Node.val == val 的节点，并返回 新的头节点 。
// 
//
// 示例 1： 
//
// 
//输入：head = [1,2,6,3,4,5,6], val = 6
//输出：[1,2,3,4,5]
// 
//
// 示例 2： 
//
// 
//输入：head = [], val = 1
//输出：[]
// 
//
// 示例 3： 
//
// 
//输入：head = [7,7,7,7], val = 7
//输出：[]
// 
//
// 
//
// 提示： 
//
// 
// 列表中的节点在范围 [0, 104] 内 
// 1 <= Node.val <= 50 
// 0 <= k <= 50 
// 
// Related Topics 链表 
// 👍 586 👎 0

package com.lucky.leetCode.editor.cn;
//Java：移除链表元素
public class P203RemoveLinkedListElements{
    public static void main(String[] args) {
        ListNode node = new ListNode(1);
        node.next = new ListNode(2);
        node.next.next = new ListNode(2);
        node.next.next.next = new ListNode(4);
        ListNode listNode = new Solution().removeElements(node, 2);
        System.out.println(listNode);
    }


    static class Solution {
        public ListNode removeElements(ListNode head, int val) {
            while (head !=null && head.val == val){
                head = head.next;
            }
            if (head == null){
                return head;
            }
            ListNode pre = head;
            while (pre.next != null){
                if (pre.next.val == val){
                    pre.next = pre.next.next;
                }else{
                    pre = pre.next;
                }
            }
            return head;
        }
    }
   static class ListNode {
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
}