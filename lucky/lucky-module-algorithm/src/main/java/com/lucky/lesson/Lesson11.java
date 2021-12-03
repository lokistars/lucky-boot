package com.lucky.lesson;

import com.lucky.design.Node;
import com.lucky.design.TreeNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 二叉树的先序、层级遍历序列化
 * 将N叉树编码为二叉树
 * @author: Loki
 * @data: 2021-11-25 14:58
 **/
public class Lesson11 {


    /**
     * 求二叉树最宽的层
     */
    private static int maxWidth(Node head){
        if (head == null){
            return 0;
        }
        Queue<Node> queue = new LinkedList<>();
        queue.add(head);
        Node curEnd = head;
        Node nextEnd = null;
        int max = 0;
        int curLevelNodes = 0;
        while (!queue.isEmpty()){
            Node cur = queue.poll();
            if (cur.left != null){
                queue.add(cur.left);
                nextEnd = cur.left;
            }
            if (cur.right != null){
                queue.add(cur.right);
                nextEnd = cur.right;
            }
            curLevelNodes ++;
            if (cur == curEnd){
                max = Math.max(max,curLevelNodes);
                curLevelNodes = 0;
                curEnd = nextEnd;
            }
        }
        return max;
    }

    /**
     * 将N叉树编码为二叉树
     */
    private static TreeNode encode(Node root){
        if (root == null){
            return null;
        }
        TreeNode head = new TreeNode(root.val);
        head.left = en(root.children);
        return head;
    }

    private static TreeNode en(List<Node> children) {
        TreeNode head = null;
        TreeNode cur = null;
        for (Node child : children) {
            TreeNode node = new TreeNode(child.val);
            if (head == null){
                head = node;
            }else {
                cur = node;
                cur.left =en(child.children);
            }
        }
        return head;
    }

    private static Node decode(TreeNode root){
        if (root == null){
            return null;
        }
        return new Node(root.val,de(root.left));
    }

    private static List<Node> de(TreeNode root){
        List<Node> children = new ArrayList<>();
        while (root != null){
            Node cur = new Node(root.val, de(root.left));
            children.add(cur);
            root = root.right;
        }
        return children;
    }
}
