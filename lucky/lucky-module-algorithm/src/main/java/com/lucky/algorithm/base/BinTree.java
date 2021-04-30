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
     * 二分查找
     * @param arr  有序数组
     * @param num  查询数据
     * @return
     */
    public static boolean dichotomy(int[] arr, int num){
        int left = 0;
        int middle = 0;
        int right = arr.length -1;
        while (left<right){
            middle = left+ ((right-left)>>1);
            if (arr[middle] == num){
                return true;
                // 如果中间这个数大于需要查找的数
            }else if(arr[middle]>num){
                right = middle -1;
            }else{
                left = middle +1;
            }
        }
        return arr[left] == num;
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
        int[] arr = {4,5,7,8,9,3,2,12,13,14,15,18};
        System.out.println(dichotomy(arr, 2));
    }
}
