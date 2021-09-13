package com.lucky.algorithm.base;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * 排序
 *
 * @author Nuany
 */
public class Sorted {


    public static void main(String[] args) {
        int[] arr = sortDate(7);
        long l = System.currentTimeMillis();
        //insertionSort(arr);
        long l1 = System.currentTimeMillis();
        System.out.println((l1 - l));
        // 并归排序
        //new MergeSort().process(arr,0,arr.length-1);
        new MergeSort().process1(arr);
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + ",");
        }
    }

    /**
     * 依次比较相邻的两个数，将小数放在前面，大数放在后面
     *
     * @param arr
     */
    public static void BubblingSort(int[] arr) {
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
     * 第一个数和第二个数进行比较 如果比他大挪到后面的位置 直到最大的那个数排在最后
     *
     * @param arr
     */
    public static void SelectionSorts(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        for (int i = 0; i < arr.length - 1; i++) {
            int minIndex = i;
            // 选择出最大的那个数,然后放在排序的最后
            for (int j = i + 1; j < arr.length; j++) {
                minIndex = arr[j] < arr[minIndex] ? j : minIndex;
            }
            swap(arr, i, minIndex);
        }
    }

    /**
     * 将一个数据插入到已经排好序的有序数据中,从而得到一个新的、个数加一的有序数据插入
     * 将一个数想前一个数比较 比他大或者小(自定义规则), 互换位置,如条件不成立 不需要互换位置, 因为之前的数是有序的
     *
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

    public static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    public static int[] sortDate(int num) {
        Random ra = new Random();
        int[] arr = new int[num];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (int) (Math.random() * 30) + 1;
        }
        return arr;
    }

    /**
     * 归并
     */
    static class MergeSort {
        /**
         * arr数组 left - right 有序  递归方式
         *
         * @param arr
         * @param left
         * @param right
         */
        public void process(int[] arr, int left, int right) {
            if (left == right) {
                return;
            }
            // 计算出 m 一半的值
            int mid = left + ((right - left) >> 1);
            //计算左半部分
            process(arr, left, mid);
            //计算右半部分
            process(arr, mid + 1, right);
            // 左边和右边合并。
            merge(arr, left, mid, right);
        }

        public void process1(int[] arr) {
            if (arr != null && arr.length < 2) {
                return;
            }
            int len = arr.length;
            //定义步长
            int mergeSize = 1;
            while (mergeSize < len) {
                //步长左边第一个位置
                int left = 0;
                while (left < len) {
                    //mid 计算步长一半的值进行merge
                    int mid = left + mergeSize - 1;
                    if (mid >= len) {
                        break;
                    }
                    //步长右边的数
                    int right = mid + mergeSize >= len - 1 ? len - 1 : mid + mergeSize;
                    merge(arr, left, mid, right);
                    left = right + 1;
                }
                // 边界条件, 步长*2不能大于数组长度
                if (mergeSize > len / 2) {
                    break;
                }
                // 步长*2
                mergeSize <<= 1;
            }
        }

        /**
         * 每一个数左边比当前数小的数累加起来
         * 数组中的两个数字，如果前面一个数字大于后面的数字，则这两个数字组成一个逆序对
         * 返回arr中有多少个子数组的累加和在lower和upper范围上
         *
         * @param arr
         * @param left
         * @param mid
         * @param right
         */
        public void merge(int[] arr, int left, int mid, int right) {
            int ans = 0;
            // 计算出一个数在右边有多少个数*2后依然小于这个数。
            int winR = mid + 1;
            for (int i = left; i <= mid; i++) {
                while (winR <= right && arr[i] > (arr[winR] * 2)) {
                    winR++;
                }
                ans += winR - mid - 1;
            }
            // 定义一个数组 存放 left - right 的数
            int[] arrCopy = new int[(right - left) + 1];
            // 定义两个指针 左组数 l  右组数 r
            int i = 0, l = left, r = mid + 1;
            // 判断 arr 的left 数和right 数那个大 拷贝谁
            while (l <= mid && r <= right) {
                // 计算小和问题 右组数有多少个大于 arr[l] 右组数 减去当前指针+1 等于当前个数
                // 数组中有多少个逆序对 小于更改大于 不需乘以他的个数
                ans += arr[l] < arr[r] ? arr[l] * (right - r + 1) : 0;
                // 如果左边数小于右边数 拷贝左边
                arrCopy[i++] = arr[l] < arr[r] ? arr[l++] : arr[r++];
            }
            while (l <= mid) {
                arrCopy[i++] = arr[l++];
            }
            while (r <= right) {
                arrCopy[i++] = arr[r++];
            }
            for (int j = 0; j < arrCopy.length; j++) {
                arr[left + j] = arrCopy[j];
            }
        }
    }

    /**
     * 随机
     */
    static class RandomSort {

        public static void main(String[] args) {
            int[] arr = sortDate(7);
            System.out.println(Arrays.toString(arr));
            process(arr,0,arr.length-1);
            System.out.println(Arrays.toString(arr));
        }
        private static void process(int[] arr,int L,int R){
            if (L>=R){
                return ;
            }
            swap(arr,L + (int) (Math.random() * (R-L+1)),R);
            int[] area = partition(arr,L,R);
            process(arr,L,area[0]-1);
            process(arr,area[1]+1,R);
        }

        /**
         * 小于目标数的在左边，大于目标数在右边
         * @param arr
         * @param L
         * @param R 以R为目标数
         * @return O(NlogN)
         */
        private static int[] partition(int[] arr,int L,int R){
            if (L>R){ // 如果L大于R 就不是一个有效范围
               return new int[]{-1,-1};
            }
            if (L==R){ // 只有一个数的情况下 L等于R
                return new int[]{L,R};
            }
            int less = L-1; // 左边界
            int more = R;   // 右边界
            while (L < more){ //从L开始小于右边界
                if (arr[L] == arr[R]){ // 如果当前数等于目标数当前数跳下一个
                    L++;
                }else if(arr[L]<arr[R]){ // 当前数跳下一个左边界向右扩
                    swap(arr,L++,++less);
                }else{
                    swap(arr,L,--more);// 右边界向左扩
                }
            }
            swap(arr,more,R);
            return new int[]{less+1,more};
        }
    }

    /**
     * 堆
     */
    static class HeapSort {
        public static void main(String[] args) {
            int[] arr = sortDate(7);
            System.out.println(Arrays.toString(arr));
            System.out.print("");
            heapSort(arr);
            System.out.println(Arrays.toString(arr));
        }

        public static void heapSort(int[] arr){
            if (arr == null || arr.length<2){
                return ;
            }
            for (int i = 0; i < arr.length; i++) {
                heapInsert(arr,i);
            }
            int heapSize = arr.length;
            swap(arr,0,--heapSize);
            while (heapSize>0){
                heapIfy(arr,0,heapSize);
                swap(arr,0,--heapSize);
            }
        }


        /**
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
            int left = index *2 +1;
            while(left <heapSize){
                int largest = left +1 <heapSize && arr[left+1] >arr[left]?left+1:left;
                largest = arr[largest] > arr[index]?largest:index;
                if (largest == index){
                    break;
                }
                swap(arr,largest,index);
                index = largest;
                left = index * 2+1;
            }
        }
    }
}
