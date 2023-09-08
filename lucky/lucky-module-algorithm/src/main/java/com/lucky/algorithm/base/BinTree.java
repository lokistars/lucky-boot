package com.lucky.algorithm.base;

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


    /**
     * 打印一个数的 32位
     * @param num
     */
    public static void print(int num){
        for(int i = 31; i>=0;i--){
            //System.out.print((num&(1<<i))== 0 ? "0":"1");
            System.out.print((num>>i)& 1);
        }
        System.out.println( );
    }
    public static void main(String[] args) {
        print(1);
        System.out.println(6>>2);
    }
}
