package com.lucky.lesson;

import com.lucky.design.Node;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * 单链表和二叉树的先序、中序、后序遍历
 * @author: Loki
 * @data: 2021-11-15 13:37
 **/
public class Lesson10 {
    private Node fast;
    private Node head;

    public static void main(String[] args) {


    }

    /**
     * 单链表
     * 给定两个可能有环也可能无环的链表,头节点head1和head2,
     * 如果两个链表相交返回相交第一个节点
     * 使用快慢指针,当两个指针相遇后快指针回到头节点
     * 判断链表是否有环,返回交叉的节点
     */

    private static Node getIntersectNode(Node head1,Node head2){
        if (head1 == null || head2 == null){
            return null;
        }
        Node loop1 = getLoopNode(head1);
        Node loop2 = getLoopNode(head2);
        if (loop1 == null && loop2 == null){
            return noLoop(head1,head2);
        }
        if (loop1 != null && loop2 != null){
            return bothLoop(head1,head2);
        }
        return null;
    }

    private static Node bothLoop(Node head1,Node head2){



        return null;
    }

    /**
     * 两个链表无环的情况下判断是否有相交
     */
    private static  Node noLoop(Node head1,Node head2){
        if (head1 == null || head2 == null){
            return null;
        }
        Node cur1 = head1;
        Node cur2 = head2;
        int len = 0;
        while (cur1.next != null){
            len++;
            cur1 = cur1.next;
        }
        while (cur2.next != null){
            len--;
            cur2 = cur2.next;
        }
        if (cur1 != cur2){
            return null;
        }
        cur1 = len>0?head1 :head2;
        cur2 = cur1 == head1? head2:head1;
        len = Math.abs(len);
        while (len!=0){
            len --;
            cur1 = cur1.next;
        }
        while (cur1 != cur2){
            cur1 = cur1.next;
            cur2 = cur2.next;
        }
        return cur1;
    }

    private static Node getLoopNode(Node head){
        if (head == null || head.next == null || head.next.next == null){
            return null;
        }
        Node slow = head.next;
        Node fast = head.next.next;
        while (slow != fast){
            if (fast.next == null || fast.next.next == null){
                return null;
            }
            fast = fast.next.next;
            slow = slow.next;
        }
        fast = head;
        while (slow != fast){
            slow = slow.next;
            fast = fast.next;
        }
        return slow;
    }

    /**
     * 二叉树的先序、中序、后序遍历
     * 先序: 任何子树的处理顺序都是，先头节点、再左子树、然后右子树
     * 中序: 任何子树的处理顺序都是，先左子树、再头节点、然后右子树
     * 后序: 任何子树的处理顺序都是，先左子树、再右子树、然后头节点
     */

    private void recursion(Node head){
        if (head == null){
            return;
        }
        recursion(head.left);
        recursion(head.right);
    }

    /**
     * 先序排序, 后序排序在heap.pop()的时候压入到另外一个栈
     */
    private  void preorderTraversal(Node head){
        if (head == null){
            return;
        }
        Stack<Node> heap = new Stack<>();
        heap.push(head);
        while (!heap.isEmpty()){
            heap.pop();
            if (head.right != null){
                heap.push(head.right);
            }
            if (head.left != null){
                heap.push(head.left);
            }
        }
    }

    /**
     *  二叉树按层遍历
     */
    private static void level(Node head){
        if (head == null){
            return;
        }
        Queue<Node> queue = new LinkedList<>();
        queue.add(head);
        while (!queue.isEmpty()){
            Node cur = queue.poll();
            if (cur.left != null){
                queue.add(head.left);
            }
            if (cur.right != null){
                queue.add(head.right);
            }
        }
    }
}
