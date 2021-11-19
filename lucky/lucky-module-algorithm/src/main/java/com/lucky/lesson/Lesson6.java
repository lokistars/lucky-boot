package com.lucky.lesson;

/**
 * 堆和堆排序
 *
 * @author: Loki
 * @data: 2021-11-15 08:54
 **/
public class Lesson6 {



    public static void heapSort(int[] arr){
        if (arr == null || arr.length<2){
            return ;
        }
        //O(N*logN)
        /*for (int i = 0; i < arr.length; i++) {
                heapInsert(arr,i);
          }*/
        // O(N)
        for (int i = arr.length -1; i >=0;  i--) {
            heapIfy(arr,i,arr.length);
        }
        int heapSize = arr.length;
        //把第一个位置和最后一个位置交换
        swap(arr,0,--heapSize);
        while (heapSize>0){
            // 交换位置后第一个位置调整堆结构
            heapIfy(arr,0,heapSize);
            swap(arr,0,--heapSize);
        }
    }


    /**
     *  O(logN)
     * 一个数插入到堆中,大根堆获取父节点 (i-1)/2
     * 获取一个数的左孩子 i*2 +1
     * 获取一个数的右孩子 i*2 +2
     * @param arr
     * @param index
     */
    private static void heapInsert(int[] arr, int index) {
        while (arr[index] > arr[(index - 1) / 2]) {
            swap(arr, index, (index - 1) / 2);
            index = (index - 1) / 2;
        }
    }

    /** 从index位置向下看是否堆结构
     * @param arr
     * @param index
     * @param heapSize
     */
    private static void heapIfy(int[] arr, int index, int heapSize) {
        // 计算左孩子下标
        int left = index *2 +1;
        while(left <heapSize){
            // 如果存在右孩子并且右孩子大于左孩子的值,返回最大孩子的下标
            int largest = left +1 <heapSize && arr[left+1] >arr[left]?left+1:left;
            //最大的孩子如果比父大返回最大孩子下标,没有PK过 break;
            largest = arr[largest] > arr[index]?largest:index;
            if (largest == index){
                break;
            }
            //最大孩子跟父交换位置
            swap(arr,largest,index);
            index = largest;
            // 左孩子继续计算它的下一个左孩子
            left = index * 2+1;
        }
    }

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
