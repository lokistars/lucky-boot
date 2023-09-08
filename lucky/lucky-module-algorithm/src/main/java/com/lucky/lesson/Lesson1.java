package com.lucky.lesson;

/**
 * 时间复杂度 : 评估一个算法需要精确到常数时间的操作
 * 常数不受数据量的影响所以不需要管,忽略掉常数只需要最高阶
 * 常数时间操作,(数值运算),数组寻址,数组在内存在是一个连续结构的,可以直接到具体下标
 * 不是固定时间: 比如链表,是一个跳转结构,不是连续的,需要循环查找
 * O(1) O(logN) O(N) O(N*logN) O(N^2) O(N^3)··· O(2^N) O(3^N) O(N!)
 * 二分查找
 * 寻找峰值
 * @author: Loki
 * @data: 2021-11-11 15:12
 **/
public class Lesson1 {
    /**
     * N个数看一遍,找到最小值 (比较)
     * 最小值放到0的位置
     * 0-N-1的位置 看一遍+比较进行一次比较
     * 1-N-1的位置 看一遍+比较进行一次比较
     * O(N2)
     *
     * @param args
     */
    public static void main(String[] args) {

    }

    /**
     * 从0出发 找到最小值 放到 0位置
     * 1~n-1 找到最小值 放到 1位置
     * 第一个数和第二个数进行比较 如果比他大挪到后面的位置 直到最大的那个数排在最后
     * O(N2)
     *
     * @param arr
     */
    private static void selectionSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        for (int i = 0; i < arr.length; i++) {
            int minIndex = i;
            for (int j = 0; j < arr.length; j++) {
                minIndex = arr[j] < arr[minIndex] ? j : minIndex;
            }
            swap(arr, i, minIndex);
        }
    }

    /**
     * 0~1 范围 谁大谁往后
     * 依次比较相邻的两个数，将小数放在前面，大数放在后面
     * O(N2)
     *
     * @param arr
     */
    private static void bubblingSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        for (int i = 0; i < arr.length - 1; i++) {
            //第一个元素和第二个匹配,比他大进行交换
            for (int j = 0; j < arr.length - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                }
            }
        }
    }

    /**
     * 0~0位置有序
     * 0~2位置有序
     * 将一个数据插入到已经排好序的有序数据中,从而得到一个新的、个数加一的有序数据插入
     * 将一个数想前一个数比较 比他大或者小(自定义规则), 互换位置,如条件不成立 不需要互换位置, 因为之前的数是有序的
     * O(N2)
     * @param arr
     */
    public static void insertionSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int index = i;
            while (index > 0) {
                if (arr[index] < arr[index - 1]) {
                    swap(arr, index, index - 1);
                    index--;
                } else {
                    break;
                }
            }
            /*for (int j =i-1; j >=0 && arr[j] >arr[j+1]; j--) {
                swap(nums,j,j+1)
            }*/
        }
    }

    /**
     * 二分查找
     * @param arr  有序数组
     * @param num  查询数据
     * @return
     */
    private static boolean dichotomy(int[] arr, int num){
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
     * 查找局部最小数
     * @param arr
     * @return
     */
    private static int getLessIndex(int[] arr){
        if (arr == null || arr.length == 0){
            return -1;
        }
        if (arr.length ==1 || arr[0] == arr[1]){
            return 0;
        }
        if (arr[arr.length-1] < arr[arr.length-2]){
            return arr.length-1;
        }
        int left = 1;
        int right = arr.length-1;
        int mid = 0;
        while(left<right){
            mid = (left+right) /2;
            if (arr[mid] > arr[mid-1]){
                right = mid-1;
            }else if (arr[mid] > arr[mid+1]){
                left = mid+1;
            }else {
                return mid;
            }
        }
        return left;
    }

    /**
     * 寻找峰值
     * 峰值元素是指其值严格大于左右相邻值的元素
     * @param arr 数组
     * @return
     */
    private static int findPeakElement(int[] arr){
        if (arr == null || arr.length == 0){
            return -1;
        }
        int size = arr.length;

        if (size == 1 || arr[0]> arr[1]) {
            return 0;
        }
        if (arr[size-1] > arr[size-2]){
            return size-1;
        }
        int l=1,m,r=size-2,ans=-1;
        while (l < r){
            m = l + ((r-l)>>1);
            if (arr[m] < arr[m-1]){
                r = m-1;
            } else if (arr[m] < arr[m+1]) {
                l = m+1;
            }else{
                ans = m;
                break;
            }
        }
        return ans;
    }

    /**
     * 随机生成数组
     * @param maxSize 随机大小
     * @param maxValue 随机值
     * @return 数组
     */
    private  static int[] generateRandomArray(int maxSize,int maxValue){
        // Math.random() [0,1) 所有小数等概率返回一个
        int[] arr = new int[(int)((maxSize +1)*Math.random())];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (int)((maxValue+1)*Math.random())
                    -(int)(maxValue*Math.random());
        }
        return arr;
    }




    private static void swap(int[] arr, int i, int j) {
        int tem = arr[i];
        arr[i] = arr[j];
        arr[j] = tem;
    }


}
