package com.algorithm;

/**
 * @author: Loki
 * @data: 2021-10-01 13:33
 **/
public class SortText {

    /**
     * 已知一个几乎有序数组，排好序移动位置不超过K
     * @param args
     */
    public static void main(String[] args) {

    }

    /**
     * 给一个数 调整到堆结构
     * @param arr
     * @param index
     * @param heapSize
     */
    private  static void heapIfy(int[] arr,int index, int heapSize){
        int left = index *2 +1;
        while (left < heapSize){
            //找出最大孩子
            int num = left+1 <heapSize && arr[left+1] > arr[left]?left+1:left;
            // 最大孩子跟父比较
            num = arr[index]>arr[num] ?index : num;
            if (num == index){
                break;
            }
            //最大孩子跟父交换位置
            swap(arr,num,index);
            index = num;
            left = index * 2 +1 ;
        }
    }

    private static void swap(int[] arr,int i,int j){
        int tem = arr[i];
        arr[i] = arr[j];
        arr[j] = tem;
    }
}
