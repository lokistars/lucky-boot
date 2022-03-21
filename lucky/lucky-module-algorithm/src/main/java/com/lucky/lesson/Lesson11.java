package com.lucky.lesson;

import com.lucky.design.Node;
import com.lucky.design.TreeNode;

import java.util.*;

/**
 * 二叉树的先序、层级遍历序列化
 * 将N叉树编码为二叉树
 * @author: Loki
 * @data: 2021-11-25 14:58
 **/
public class Lesson11 {

    /**
     * 先序 序列化
     */
    private static Queue<String> preSerial(Node head){
        Queue<String> ans = new LinkedList<>();
        pre(head,ans);
        return ans;
    }
    private static Queue<String> pre(Node head,Queue<String> ans){
        if (head == null){
            ans.add(null);
        }else {
            ans.add(String.valueOf(head.val));
            pre(head.left,ans);
            pre(head.right,ans);
        }
        return ans;
    }
    /**
     * 队列反序列化到二叉树
     * 先让左树遍历出来,然后在右树
     */
    private static Node buildPreQueue(Queue<String> pre){
        if (pre == null || pre.isEmpty()){
            return null;
        }
        return preQueue(pre);
    }

    private static Node preQueue(Queue<String> pre){
        String poll = pre.poll();
        if (poll == null){
            return null;
        }
        Node head = new Node(Integer.valueOf(poll));
        head.left = preQueue(pre);
        head.right = preQueue(pre);
        return head;
    }

    /**
     * 求二叉树最宽的层,按层遍历,先左后右
     *
     */
    private static int maxWidth(TreeNode head){
        if (head == null){
            return 0;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(head);
        // 当前层最右节点,下一层最右节点
        TreeNode curEnd = head;
        TreeNode nextEnd = null;
        int max = 0;
        int curLevelNodes = 0;
        while (!queue.isEmpty()){
            TreeNode cur = queue.poll();
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
     * 将N叉树编码为二叉树,将多叉树第一个儿子作为二叉树左节点,将其兄弟节点作为二叉树右节点,
     * 左儿子,右兄弟
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
        if (children == null){
            return null;
        }
        TreeNode head = null;
        TreeNode cur = null;
        for (Node child : children) {
            TreeNode node = new TreeNode(child.val);
            if (head == null){
                head = node;
            }else {
                cur.right = node;
            }
            cur = node;
            cur.left =en(child.children);
        }
        return head;
    }

    /**
     * 二叉树转换为N插树
     */
    private static Node decode(TreeNode root){
        if (root == null){
            return null;
        }
        return new Node(root.val,de(root.left));
    }

    private static List<Node> de(TreeNode root){
        if (root == null){
            return null;
        }
        List<Node> children = new ArrayList<>();
        while (root != null){
            Node cur = new Node(root.val, de(root.left));
            children.add(cur);
            root = root.right;
        }
        return children;
    }

    /**
     * 找一个node节点的后继节点
     * 在中序遍历中 node节点的下一个节点就是它后继节点
     * 一个节点有右子树，那么它的下一个节点就是它的右子树中的最左子节点。
     * 一个节点没有右子树时分两种情况
     * 当前节点是它父节点的左子节点，那么它的下一个节点就是它的父节点
     * 当前节点是它父节点的右子节点，此时沿着指向父节点的指针一直向上遍历，
     * 直到找到一个是它父节点的左子节点的节点，如果这个节点存在，那么这个节点的父节点就是我们要找的下一个节点
     */
    public static Node getNextNode(Node node) {
        if (node == null) {
            return node;
        }
        if (node.right != null) {
            return getLeftMost(node.right);
        } else {
            Node parent = node.parent;
            //整棵数的最右节点没有后继节点，因此加上parent != null。
            while (parent != null && parent.left != node) {
                //不断往上走
                node = parent;
                parent = node.parent;
            }
            return parent;
        }
    }

    public static Node getLeftMost(Node node) {
        if (node == null) {
            return node;
        }
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }
}
