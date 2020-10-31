package com.lucky.platform.base;

/**
 * @author Nuany
 */
public class BinTree {
    private int data;
    BinTree left;
    BinTree right;

    public BinTree(int data){
        this.data = data;
        this.left = null;
        this.right= null;
    }


    public  void insert(BinTree root, int data){
        //判断根节点,如果根节点比data 数小 插入右边
        if (root.data < data){
            if (root.right == null){
                root.right = new BinTree(data);
            }else {
                insert(root.right,data);
            }
        }else {
            if (root.left == null){
                root.left = new BinTree(data);
            }else {
                insert(left.right,data);
            }
        }
    }


    public static void main(String[] args) {
        System.out.println(6>>2);
    }
}
