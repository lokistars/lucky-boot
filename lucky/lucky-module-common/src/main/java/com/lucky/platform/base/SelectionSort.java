package com.lucky.platform.base;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Nuany
 */
public class SelectionSort {



    public static void main(String[] args) {
        int[] arr = {1,3,9,5,3,7};
        SelectionSorts(arr);
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
        }
    }

    /**
     * 选着排序
     * @param arr
     */
    public static void SelectionSorts(int[] arr){
        if (arr == null || arr.length < 2){
            return;
        }
        for (int i = 0; i< arr.length -1 ;i++){
            int minIndex = i;
            for (int j = i+1; j< arr.length ; j++){
                minIndex = arr[j]< arr[minIndex] ? j:minIndex;
            }
            swap(arr,i,minIndex);
        }
    }
    public static void swap(int arr[], int i,int j){
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    public void sortDate(){

    }
}
