package com.lucky.lesson;

import java.util.Arrays;

/**
 * 堆和堆排序
 *
 * @author: Loki
 * @data: 2021-11-15 08:54
 **/
public class Lesson6 {



    public static void main(String[] args) {
        int[] arr = new int[]{1, 3, 4, 9, 2, 5, 6};
        heapSort1(arr);

        System.out.println(Arrays.toString(arr));
    }
    public static void heapSort1(int[] arr) {
        for (int i = arr.length - 1; i >= 0; i--) {
            siftDown(arr, i, arr[i], arr.length);
        }
        System.out.println(Arrays.toString(arr));
        int heapSize = arr.length;
        // 依次取出小根堆的堆顶元素
        while (heapSize > 0) {
            int num = arr[0];
            --heapSize;
            siftDown(arr, 0,arr[heapSize],heapSize);
            arr[heapSize] = num;
            System.out.println(Arrays.toString(arr));
        }

    }

    public static void heapSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        //O(N*logN)
        /*for (int i = 0; i < arr.length; i++) {
                heapInsert(arr,i);
          }*/
        // O(N)
        for (int i = arr.length - 1; i >= 0; i--) {
            heapIfy(arr, i, arr.length);
        }
        int heapSize = arr.length;
        // 依次取出大根堆的堆顶元素，放到最后,得到排序结果
        while (heapSize > 0) {
            swap(arr, 0, --heapSize);
            // 交换位置后第一个位置调整堆结构
            heapIfy(arr, 0, heapSize);
            //swap(arr, 0, --heapSize);
        }
    }

    /**
     * O(logN),让当前节点和父节点比较,如果比父节点小就交换
     * 一个数插入到堆中,大根堆获取父节点 (i-1)/2
     * 获取一个数的左孩子 i*2 +1
     * 获取一个数的右孩子 i*2 +2
     *
     * @param arr   数组
     * @param index
     */
    private static void heapInsert(int[] arr, int index) {
        // 获取父节点可以改写成 (k - 1) >>> 1
        while (arr[index] > arr[(index - 1) / 2]) {
            swap(arr, index, (index - 1) / 2);
            index = (index - 1) / 2;
        }
    }

    /**
     * 从index位置向下看是否堆结构,大根堆实现
     * @param arr      数组
     * @param index    当前节点索引位置
     * @param heapSize 堆在的数据
     */
    private static void heapIfy(int[] arr, int index, int heapSize) {
        // 计算左孩子下标, 可以写成 index *2 +1
        int left = (index << 1) + 1;
        while (left < heapSize) {
            // 如果存在右孩子并且右孩子大于左孩子的值,返回最大孩子的下标
            int largest = left + 1 < heapSize && arr[left + 1] > arr[left] ? left + 1 : left;

            //最大的孩子如果比父大,没有PK过 break;
            if (arr[index] > arr[largest]) {
                break;
            }
            //最大孩子跟父交换位置
            swap(arr, largest, index);
            index = largest;
            // 左孩子继续计算它的下一个左孩子
            left = index * 2 + 1;
        }
    }

    /**
     * jdk中的实现方式,小根堆, 当前节点从index位置出发,判断是否需要进行下沉
     * @param arr 数组
     * @param index 从头节点开始检查
     * @param num 当前节点
     * @param heapSize 堆大小
     */
    private static void siftDown(int[] arr, int index, int num, int heapSize) {
        int half = heapSize >>> 1;
        //int num = arr[index];
        while (index < half) {
            // 获取左孩子节点
            int child = (index << 1) + 1;

            // 左孩子比右孩子小返回左孩子,获取最小的孩子节点
            int least = child + 1 < heapSize && arr[child + 1] < arr[child] ? child + 1 : child;

            // 当前节点小于最小孩子节点 跳过循环
            if (num < arr[least]) {
                break;
            }
            // 最小孩子节点跟最顶层节点交换位置,丢失最顶层节点数据
            arr[index] = arr[least];
            // 交换位置,不丢失数据
            //swap(arr,index,least);
            index = least;
        }
        arr[index] = num;
    }

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    private static int size = 0;

    /**
     * 小根堆返回头节点,需要siftDown丢失头节点数据才可以做到
     * @param arr 数组
     * @return
     */
    private static int poll(int[] arr) {
        if (size == 0) {
            size = arr.length;
        }
        if (size > 0) {
            int num = arr[0];
            --size;
            siftDown(arr, 0, arr[size], size);
            return num;
        }
        return 0;
    }

}
