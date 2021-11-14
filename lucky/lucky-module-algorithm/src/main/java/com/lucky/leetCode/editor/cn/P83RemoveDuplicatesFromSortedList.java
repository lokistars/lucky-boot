//存在一个按升序排列的链表，给你这个链表的头节点 head ，请你删除所有重复的元素，使每个元素 只出现一次 。 
//
// 返回同样按升序排列的结果链表。 
//
// 
//
// 示例 1： 
//
// 
//输入：head = [1,1,2]
//输出：[1,2]
// 
//
// 示例 2： 
//
// 
//输入：head = [1,1,2,3,3]
//输出：[1,2,3]
// 
//
// 
//
// 提示： 
//
// 
// 链表中节点数目在范围 [0, 300] 内 
// -100 <= Node.val <= 100 
// 题目数据保证链表已经按升序排列 
// 
// Related Topics 链表 
// 👍 545 👎 0

package com.lucky.leetCode.editor.cn;

import java.util.HashMap;
import java.util.Map;
// Java: 删除排序链表中的重复元素
public class P83RemoveDuplicatesFromSortedList {
    public static void main(String[] args) {
        ListNode node = new ListNode(1);
        node.next = new ListNode(2);
        node.next.next = new ListNode(2);
        node.next.next.next = new ListNode(3);
        node.next.next.next.next = new ListNode(3);
        ListNode Node = new Solution().deleteDuplicates(node);
        while (Node!= null){
            System.out.println(Node.val);
            Node = Node.next;
        }
    }

    /**
     * Definition for singly-linked list.
     */
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

    static class Solution {
        public ListNode deleteDuplicates(ListNode head) {
            Map<Integer,Integer> map = new HashMap();
            ListNode next = head;
            map.put(next.val,next.val);
            while(next.next !=null){
                if(map.containsKey(next.next.val)){
                    next.next = next.next.next;
                }else {
                    map.put(next.next.val,next.next.val);
                }
                next = next.next;
            }
            
            /*while (next.next  != null){
                if(next.val == next.next.val){
                    next.next = next.next.next;
                }
                    next = next.next;
            }*/
            return head;
        }
    }
}