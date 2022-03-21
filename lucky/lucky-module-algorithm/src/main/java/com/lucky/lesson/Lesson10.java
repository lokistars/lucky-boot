package com.lucky.lesson;

import com.lucky.design.Node;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * 单链表又环或无环是否相交
 * 二叉树的先序、中序、后序遍历
 * @author: Loki
 * @data: 2021-11-15 13:37
 **/
public class Lesson10 {

    /**
     * 单链表:
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
            return bothLoop(head1,loop1,head2,loop2);
        }
        return null;
    }

    /**
     * 两个有环链表,返回第一个相交节点,如果不相交返回null
     */
    private static Node bothLoop(Node head1,Node loop1,Node head2,Node loop2){
        Node cur1 = null;
        Node cur2 = null;
        if (loop1 == loop2){
            cur1 = head1;
            cur2 = head2;
            int i = 0;
            while (cur1 != loop1){
                i++;
                cur1 = cur1.next;
            }
            while (cur2 != loop2){
                i--;
                cur2 = cur2.next;
            }
            cur1 = i >0 ? head1 : head2;
            cur2 = cur1 == head1 ? head2:head1;
            i = Math.abs(i);
            while (i != 0){
                i --;
                cur1 = cur1.next;
            }
            while (cur1 != cur2){
                cur1 = cur1.next;
                cur2 = cur2.next;
            }
            return cur1;
        }else{
            // 如果两个有环链表,第一个相交不相等,可能情况:
            // 两个有环链表不相交,两个有环链表相交不在一个节点上
            cur1 = loop1.next;
            while (cur1 != loop1){
                if(cur1 == loop2){
                    return loop1;
                }
                cur1 = cur1.next;
            }
            return  null;
        }
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
        // 如果两个链表最后的节点没有相交那么这两个链表就没有相交
        if (cur1 != cur2){
            return null;
        }
        // 找到最长的那个链表
        cur1 = len>0?head1 :head2;
        cur2 = cur1 == head1? head2:head1;
        // 返回绝对值,
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

    /**
     * 找到链表第一个入环节点,如果无环,返回null
     */
    private static Node getLoopNode(Node head){
        if (head == null || head.next == null || head.next.next == null){
            return null;
        }
        //慢指针一次走一步,快指针一次走两步
        Node slow = head.next;
        Node fast = head.next.next;
        while (slow != fast){
            // 如果快指针提前走到null,表示链表无环
            if (fast.next == null || fast.next.next == null){
                return null;
            }
            fast = fast.next.next;
            slow = slow.next;
        }
        // 上一个循环出来快慢指针相遇,快指针回到头部,一次走一步,最后一定会相遇
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
     * 得到一个数的祖先节点:
     * 通过先序遍历左边的节点,和后续遍历右边的节点得到结果的交集,就是这个数的祖先节点
     */
    private static void recursion(Node head){
        if (head == null){
            return;
        }
        // 先序
        recursion(head.left);
        // 中序
        recursion(head.right);
        // 后序
    }

    /**
     * 先序排序, 先压右在压左, 后序：先压左在压右
     * 后序排序在heap.pop()的时候压入到另外一个栈,等所有都压入完了在遍历另一个栈
     */
    private static void preorderTraversal(Node head){
        if (head == null){
            return;
        }
        Stack<Node> heap = new Stack<>();
        heap.push(head);
        while (!heap.isEmpty()){
            head = heap.pop();
            // 先序 head
            if (head.right != null){
                heap.push(head.right);
            }
            if (head.left != null){
                heap.push(head.left);
            }
        }
    }

    /**
     * 中序遍历 :
     */
    private static void infixOrder(Node head){
        if (head != null){
            Stack<Node> stack = new Stack<>();
            while (!stack.isEmpty() || head!=null){
                if (head!= null){
                    stack.push(head);
                    head = head.left;
                }else{
                    head = stack.pop();
                    // 中序
                    head = head.right;
                }
            }
        }
    }
    /**
     *  二叉树按层遍历: 每一层从左往右,先左后右
     */
    private static void level(Node head){
        if (head == null){
            return;
        }
        Queue<Node> queue = new LinkedList<>();
        queue.add(head);
        while (!queue.isEmpty()){
            Node cur = queue.poll();
            // 按层 cur
            if (cur.left != null){
                queue.add(cur.left);
            }
            if (cur.right != null){
                queue.add(cur.right);
            }
        }
    }
}
