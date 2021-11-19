package com.lucky.lesson;

import java.util.Arrays;

/**
 * 归并排序
 * 每一个数左边比当前数小的数累加起来
 * 数组中的两个数字，如果前面一个数字大于后面的数字，则这两个数字组成一个逆序对
 * 返回arr中有多少个子数组的累加和在lower和upper范围上
 * @author: Loki
 * @data: 2021-11-13 16:46
 **/
public class Lesson4 {

    public static void main(String[] args) {
        int[] arr = {2,4,1,3};
        process(arr,0, arr.length-1);
        System.out.println(Arrays.toString(arr));
    }

    /**
     * arr数组 left - right 有序  递归方式
     *
     * @param arr
     * @param left
     * @param right
     */
    private static void process(int[] arr, int left, int right) {
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

    private static void process1(int[] arr) {
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

    private static void merge(int[] arr, int left, int mid, int right) {
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
