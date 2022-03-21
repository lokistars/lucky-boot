package com.lucky.lesson;

import com.lucky.design.TreeNode;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 二叉树基本算法
 * 判断完全二叉树
 * 1、假设以X节点为头,假设可以向X左树和X右树要任何信息,
 * 2、在上一步假设下,讨论以X为头节点的树,得到答案的可能性
 * 3、列出所有可能性后,确定到底需要向左树和右树要什么样的信息
 * 4、把左树信息和右树信息求全集,就是任何一棵树都需要返回的信息S
 * 5、递归函数都返回S,每一棵树都这么要求。
 *
 * @author: Loki
 * @data: 2021-11-25 16:17
 **/
public class Lesson12 {

    /**
     * 完全二叉树: 按层遍历
     * 有右孩子没左孩子,肯定不是完全二叉树
     * 当第一次遇到左右孩子不双全的时候,剩下遍历的节点必须是叶节点
     */
    private static boolean isCBT1(TreeNode head) {
        if (head == null) {
            return true;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        TreeNode left = null;
        TreeNode right = null;
        boolean leaf = false;
        queue.add(head);
        while (!queue.isEmpty()) {
            head = queue.poll();
            left = head.left;
            right = head.right;
            //如果遇到不双全节点,又发现不是叶子节点
            boolean type = (leaf && (left != null || right != null))
                    || (left == null && right != null);
            if (type) {
                return false;
            }
            if (left != null) {
                queue.add(left);
            }
            if (right != null) {
                queue.add(left);
            }
            if (left == null || right == null) {
                leaf = true;
            }
        }
        return true;
    }

    /**
     * 完全二叉树
     */
    private static CbtInfo process5(TreeNode head) {
        if (head == null) {
            return new CbtInfo(true, true, 0);
        }
        CbtInfo left = process5(head.left);
        CbtInfo right = process5(head.right);
        int height = Math.max(left.height, right.height) + 1;
        boolean isFull = left.isFull && right.isFull && left.height == right.height;
        boolean isCBT = false;
        if (left.isFull && right.isFull && left.height == right.height) {
            isCBT = true;
        } else if (left.isCBT && right.isFull && left.height == right.height + 1) {
            isCBT = true;
        } else if (left.isFull && right.isFull && left.height == right.height + 1) {
            isCBT = true;
        } else if (left.isFull && right.isCBT && left.height == right.height) {
            isCBT = true;
        }
        return new CbtInfo(isFull, isCBT, height);
    }

    static class CbtInfo {
        public boolean isFull;
        public boolean isCBT;
        public int height;

        public CbtInfo(boolean isFull, boolean isCBT, int height) {
            this.isFull = isFull;
            this.isCBT = isCBT;
            this.height = height;
        }
    }

    /**
     * 给定一颗二叉树头节点,判断是不是平衡二叉树
     * 每一颗子树,左树高度,右树高度相差都不会超过1
     */
    private static boolean is(TreeNode head) {
        if (head == null) {
            return true;
        }
        return process(head).isBalanced;
    }

    private static Info process(TreeNode head) {
        if (head == null) {
            return new Info(true, 0);
        }

        Info left = process(head.left);
        Info right = process(head.right);

        int height = Math.max(left.height, right.height) + 1;

        boolean isBalanced = true;

        if (!left.isBalanced) {
            isBalanced = false;
        }
        if (!right.isBalanced) {
            isBalanced = false;
        }
        // 左右两树高度差大于 1
        if (Math.abs(left.height - right.height) > 1) {
            isBalanced = false;
        }

        return new Info(isBalanced, height);
    }

    /**
     * 是否平衡,他的高度
     */
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
     * X左树上的最大值比X小, X右树上的最小值比X大
     */
    private static boolean isBST(TreeNode head) {
        if (head == null) {
            return true;
        }
        return process1(head).isBst;
    }

    private static BstInfo process1(TreeNode head) {
        if (head == null) {
            return null;
        }
        BstInfo left = process1(head.left);
        BstInfo right = process1(head.right);
        boolean isBst = true;
        int max = head.val;
        int min = head.val;
        // 整颗树的最大值 和最小值。
        if (left != null) {
            max = Math.max(max, left.max);
        }
        if (right != null) {
            max = Math.max(max, right.max);
        }

        if (left != null) {
            min = Math.min(min, left.min);
        }
        if (right != null) {
            min = Math.min(min, right.min);
        }

        if (left != null && !left.isBst) {
            isBst = false;
        }
        if (right != null && !right.isBst) {
            isBst = false;
        }

        // 左树上的最大值大于等于X, 右树上的最小值小于等于X
        if (left != null && left.max >= head.val) {
            isBst = false;
        }
        if (right != null && right.min <= head.val) {
            isBst = false;
        }
        return new BstInfo(isBst, max, min);
    }

    /**
     * 是否是搜索二叉树,树的最大值,树最小值
     */
    static class BstInfo {
        public boolean isBst;
        public int max;
        public int min;

        public BstInfo(boolean isBst, int max, int min) {
            this.isBst = isBst;
            this.max = max;
            this.min = min;
        }
    }

    /**
     * 给定一课二叉树的头节点,任何两个节点之间都存在距离,返回整颗树最大距离
     * 1、X左树的最大距离,X右树的最大距离 + 1
     * 2、
     */
    private static int maxDistance(TreeNode head) {
        return process2(head).maxDistance;
    }

    private static MaxInfo process2(TreeNode head) {
        if (head == null) {
            return new MaxInfo(0, 0);
        }
        MaxInfo left = process2(head.left);
        MaxInfo right = process2(head.right);
        // 计算高度
        int height = Math.max(left.height, right.height) + 1;
        int p1 = left.maxDistance;
        int p2 = right.maxDistance;
        int p3 = left.height + right.height + 1;
        int max = Math.max(Math.max(p1, p2), p3);
        return new MaxInfo(max, height);
    }

    /**
     * 最大距离和高度
     */
    static class MaxInfo {
        public int maxDistance;
        public int height;

        public MaxInfo(int maxDistance, int height) {
            this.maxDistance = maxDistance;
            this.height = height;
        }
    }

    /**
     * 判断一颗树是否满二叉树
     * 如果这棵树高度是X,他的节点数一定是2X-1个节点
     * height/2 -1 是否等于节点数
     */

    private static boolean FullBinaryTree(TreeNode head) {
        if (head == null) {
            return true;
        }
        final FullInfo fullInfo = process3(head);
        return (1 << fullInfo.height) - 1 == fullInfo.nodes;
    }

    private static FullInfo process3(TreeNode head) {
        if (head == null) {
            return new FullInfo(0, 0);
        }
        FullInfo left = process3(head.left);
        FullInfo right = process3(head.right);
        int height = Math.max(left.height, right.height) + 1;
        int nodes = left.nodes + right.nodes + 1;
        return new FullInfo(nodes, height);
    }

    /**
     * 节点数和高度
     */
    static class FullInfo {
        public int nodes;
        public int height;

        public FullInfo(int nodes, int height) {
            this.nodes = nodes;
            this.height = height;
        }
    }

    /**
     * 在一个二叉树中他本身不是搜索树,他的子树是二叉搜索树,
     * 整颗二叉树所有子树中找出那个最大的二叉搜索树,返回他的节点数量
     */
    private static MaxSubInfo process4(TreeNode head) {
        if (head == null) {
            return null;
        }
        MaxSubInfo left = process4(head.left);
        MaxSubInfo right = process4(head.right);
        int max = head.val;
        int min = head.val;
        int allSize = 1;
        int p1 = -1;
        if (left != null) {
            max = Math.max(max, left.max);
            min = Math.min(max, left.min);
            allSize += left.allSize;
            p1 = left.maxBstSubSize;
        }
        int p2 = -1;
        if (right != null) {
            max = Math.max(max, right.max);
            min = Math.min(max, right.min);
            allSize += right.allSize;
            p2 = right.maxBstSubSize;
        }

        int p3 = -1;
        boolean leftBst = left == null ? true : (left.maxBstSubSize == left.allSize);
        boolean rightBst = right == null ? true : (right.maxBstSubSize == right.allSize);
        if (leftBst && rightBst) {
            boolean leftMaxLessX = left == null ? true : (left.max < head.val);
            boolean rightMinMoreX = right == null ? true : (right.min > head.val);
            if (leftMaxLessX && rightMinMoreX) {
                int leftSize = left == null ? 0 : left.allSize;
                int rightSize = right == null ? 0 : right.allSize;
                p3 = leftSize + rightSize + 1;
            }
        }
        return new MaxSubInfo(allSize, max, min, Math.max(p1, Math.max(p2, p3)));
    }

    /**
     * 整颗树值,最大值,最小值,最大满足搜索二叉子树大小
     */
    static class MaxSubInfo {
        public int allSize;
        public int max;
        public int min;
        public int maxBstSubSize;

        public MaxSubInfo(int allSize, int max, int min, int maxBstSubSize) {
            this.allSize = allSize;
            this.max = max;
            this.min = min;
            this.maxBstSubSize = maxBstSubSize;
        }
    }
}
