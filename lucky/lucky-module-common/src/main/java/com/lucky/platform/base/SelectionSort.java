package com.lucky.platform.base;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * @author Nuany
 */
public class SelectionSort {



    public static void main(String[] args) {
        int[] arr = sortDate(200000);
        long l = System.currentTimeMillis();
        insertionSort(arr);
        long l1 = System.currentTimeMillis();
        System.out.println((l1-l));
        /*for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]+",");
        }*/
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
            arr[i] = ra.nextInt(num);
        }
        return arr;
    }
}
