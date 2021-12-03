package com.lucky.lesson;

import com.lucky.design.TreeNode;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 二叉树基本算法
 * 判断完全二叉树
 * @author: Loki
 * @data: 2021-11-25 16:17
 **/
public class Lesson12 {
    /**
     * 完全二叉树:
     * 有又孩子没左孩子,肯定不是完全二叉树
     * 当第一次遇到左右孩子不双全的时候,剩下遍历的节点必须是叶节点
     */
    private static boolean isCBT1 (TreeNode head){
        if (head == null){
            return true;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        TreeNode left = null;
        TreeNode right = null;
        boolean leaf = false;
        queue.add(head);
        while (!queue.isEmpty()){
            head =  queue.poll();
            left = head.left;
            right = head.right;
            boolean type = (leaf && (left != null || right != null))
                    || (left == null && right != null);
            if (type){
                return  false;
            }
            if (left != null){
                queue.add(left);
            }
            if (right != null){
                queue.add(left);
            }
            if (left == null || right == null){
                leaf = true;
            }
        }
        return true;
    }

    /**
     * 给定一颗二叉树头节点,判断是不是平衡二叉树
     * 每一颗子树,左树高度,右树高度相差都不会超过1
     */
    private static boolean is(TreeNode head){
        if (head == null){
            return true;
        }
        return process(head).isBalanced;
    }

    private static Info process(TreeNode head){
        if (head == null){
            return new Info(true,0);
        }

        Info left = process(head.left);
        Info right = process(head.right);

        int height = Math.max(left.height, right.height) +1 ;

        boolean isBalanced = true;

        if (!left.isBalanced){
            isBalanced = false;
        }
        if (!right.isBalanced){
            isBalanced = false;
        }

        if (Math.abs(left.height - right.height) >1){
            isBalanced = false;
        }

        return new Info(isBalanced,height);
    }

    static class Info {
        public boolean isBalanced;
        public int height;

        public Info(boolean isBalanced, int height) {
            this.isBalanced = isBalanced;
            this.height = height;
        }
    }

    /**
     * 判断二叉树是否是搜索二叉树
     * 每一课子树的头节点,左边比头小,右边比头大,才是搜索二叉树,没有重复值
     */
    private static boolean isBST(TreeNode head){
        if (head == null){
            return true;
        }
        return process1(head).isBst;
    }

    private static InfoBst process1(TreeNode head){
        if (head == null){
            return null;
        }
        InfoBst  left  = process1(head.left);
        InfoBst  right  = process1(head.right);
        boolean isBst = true;
        int max  = head.val;
        int min = head.val;

        if (left != null){
            max = Math.max(max,left.max);
        }
        if (right != null){
            max = Math.max(max,right.max);
        }

        if (left != null){
            min = Math.min(min,left.min);
        }
        if (right != null){
            min = Math.min(min,left.min);
        }

        if (left!=null && !left.isBst){
            isBst = false;
        }

        if (right!=null && !right.isBst){
            isBst = false;
        }
        if (left != null && left.max >= head.val){
            isBst = false;
        }
        if (left != null && right.min <= head.val){
            isBst = false;
        }
        return new InfoBst(isBst,max,min);
    }


    static class InfoBst {
        public boolean isBst;
        public int max;
        public int min;

        public InfoBst(boolean isBst, int max, int min) {
            this.isBst = isBst;
            this.max = max;
            this.min = min;
        }
    }

    /**
     *  给定一课二叉树的头节点,任何两个节点之间都存在距离,返回整颗树最大距离
     *  1、X左树的最大距离,X右树的最大距离 + 1
     */
    private static int maxDistance(TreeNode head){
        return process2(head).maxDistance;
    }

    private static MaxInfo process2(TreeNode head){
        if (head == null){
            return new MaxInfo(0,0);
        }
        MaxInfo left = process2(head.left);
        MaxInfo right = process2(head.right);
        int height = Math.max(left.height, right.height) + 1;
        int p1 = left.maxDistance;
        int p2 = right.maxDistance;
        int p3 = left.height + right.height +1;
        int max = Math.max(Math.max(p1,p2),p3);
        return new MaxInfo(max,height);
    }

    static class MaxInfo{
        public int maxDistance;
        public int height;
        public MaxInfo(int maxDistance,int height){
            this.maxDistance = maxDistance;
            this.height = height;
        }
    }

    /**
     * 判断一颗树是否满二叉树
     */
    private static MaxInfo process3(TreeNode head){
        return null;
    }
}
