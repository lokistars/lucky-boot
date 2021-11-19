package com.lucky.lesson;

/**
 * 归并+ 随机快排
 * @author: Loki
 * @data: 2021-11-15 00:40
 **/
public class Lesson5 {
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

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
