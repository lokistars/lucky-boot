package com.lucky.algorithm.base;

import java.util.Random;

/**
 * 排序
 * @author Nuany
 */
public class SelectionSort {



    public static void main(String[] args) {
        int[] arr = sortDate(7);
        long l = System.currentTimeMillis();
        //insertionSort(arr);
        long l1 = System.currentTimeMillis();
        System.out.println((l1-l));
        // 并归排序
        //new MergeSort().process(arr,0,arr.length-1);
        new MergeSort().process1(arr);
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]+",");
        }
    }

    /**
     * 选着排序
     * 第一个数和第二个数进行比较 如果比他大挪到后面的位置 直到最大的那个数排在最后
     * @param arr
     */
    public static void SelectionSorts(int[] arr){
        if (arr == null || arr.length < 2){
            return;
        }
        for (int i = 0; i< arr.length -1 ;i++){
            int minIndex = i;
            // 选择出最大的那个数,然后放在排序的最后
            for (int j = i+1; j< arr.length ; j++){
                minIndex = arr[j]< arr[minIndex] ? j:minIndex;
            }
            swap(arr,i,minIndex);
        }
    }

    /**
     *  冒泡排序
     *  依次比较相邻的两个数，将小数放在前面，大数放在后面
     * @param arr
     */
    public static void BubblingSort(int[] arr){
        for (int i = 0; i < arr.length-1; i++) {
            //第一个元素和第二个匹配,比他大进行交换
            for (int j= i; j < arr.length-1; j++) {
                if(arr[j]>arr[j+1]){
                    swap(arr,j, j+1);
                }
            }
        }
    }

    /**
     * 插入排序
     * 将一个数据插入到已经排好序的有序数据中,从而得到一个新的、个数加一的有序数据
     * 将一个数想前一个数比较 比他大或者小(自定义规则), 互换位置,如条件不成立 不需要互换位置, 因为之前的数是有序的
     * @param arr
     */
    public static void insertionSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int index = i;
            while (index >0){
                if (arr[index]<arr[index-1]){
                    swap(arr,index, index-1);
                    index --;
                }else{
                    break;
                }
            }
            /*for (int j = i; j >0 ; j--) {
                if (arr[j] <arr[j-1]){
                }
            }*/
        }
    }

    public static void swap(int[] arr, int i,int j){
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    public static int[] sortDate(int num){
        Random ra = new Random();
        int[] arr = new int [num];
        for (int i = 0; i < arr.length; i++) {
            arr[i] =(int) (Math.random()*30)+1;
        }
        return arr;
    }

    /**
     * 并归排序
     */
    static class MergeSort{

        /**
         *  arr数组 left - right 有序  递归方式
         * @param arr
         * @param left
         * @param right
         */
        public void process(int[] arr,int left,int right){
            if (left == right){ return; }
            // 计算出 m 一半的值
            int mid = left+((right-left)>>1);
            //计算左半部分
            process(arr,left,mid);
            //计算右半部分
            process(arr,mid+1,right);
            // 左边和右边合并。
            merge(arr,left,mid,right);
        }

        public void process1(int[] arr){
            if (arr != null && arr.length < 2){
                return;
            }
            int len = arr.length;
            //定义步长
            int mergeSize  = 1;
            while (mergeSize<len){
                //步长左边第一个位置
                int left = 0;
                while (left<len) {
                    //mid 计算步长一半的值进行merge
                    int mid = left + mergeSize -1;
                    if (mid>=len){
                        break;
                    }
                    //步长右边的数
                    int right = mid+mergeSize >=len-1 ? len-1:mid+mergeSize ;
                    merge(arr,left,mid,right);
                    left = right+1;
                }
                // 边界条件, 步长*2不能大于数组长度
                if (mergeSize > len /2){
                    break;
                }
                // 步长*2
                mergeSize <<= 1;
            }
        }

        public void merge(int[] arr,int left,int mid,int right){
            // 定义一个数组 存放 left - right 的数
            int[] arrCopy = new int[(right -left) +1];
            int i = 0,l = left,m= mid+1;
            // 判断 arr 的left 数和right 数那个大 拷贝谁
            while (l<=mid && m<= right){
                // 如果左边数小于右边数 拷贝左边
                arrCopy[i++] = arr[l] <= arr[m]?arr[l++]:arr[m++];
            }
            while (l <= mid){
                arrCopy[i++] =arr[l++];
            }
            while (m<=right){
                arrCopy[i++] =arr[m++];
            }
            for (int j = 0; j < arrCopy.length; j++) {
                arr[left+j] = arrCopy[j];
            }
        }
    }
}
