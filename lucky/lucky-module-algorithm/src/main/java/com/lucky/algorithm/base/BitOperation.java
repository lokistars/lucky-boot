package com.lucky.algorithm.base;

/**
 * @author: Loki
 * @data: 2021-04-14 12:08
 * 位运算 与&,或|,非~,异或^
 **/
public class BitOperation {
    /**
     * 两个数相加 等同于两个数 无进位相加 加进位信息
     *  61 + 38
     *  61 = (32+16+8+4+1)  0111101
     *  38 = (32+4+2)       0100110
     *  61 ^ 38  无进位相加
     *  61 & 38 <<1  进位信息
     * @param one
     * @param two
     * @return 加法
     */
    public static int add(int one ,int two){
        int num = one;
        while (two!=0){
            num = one^two;
            two = (one&two)<<1;
            one = num;
        }
        return num;
    }

    /**
     *  一个数加上这个数的相反数
     * @param one
     * @param two
     * @return 减法
     */
    public static int minus(int one ,int two){
        return add(one,add(~two,1));
    }

    /**
     *
     * @param one
     * @param two
     * @return 乘法
     */
    public static int multi(int one ,int two){
        int num = 0;
        while(two != 0){
            if ((two&1)!=0){
                num = add(num,one);
            }
            one <<= 1;
            two >>>= 1;
        }
        return num;
    }

    public static int st(int one ,int two){
        int num = 0;

        return num;
    }

    /**
     * 有两个数出现奇数次,其他数都出现偶数次
     * 第一次循环获取到 第一个数^第二个数
     * 提取最右侧的 1 这个数组 可以被分为两类数据, 最右侧是1和不是1
     * 是1的这个数进行^操作可以得到第一个数
     * @param arr
     * @return
     */
    public static int[] timeNum2(int[] arr){
        int eor = 0;
        for (int i = 0; i < arr.length; i++) {
            eor ^= arr[i];
        }
        int rightOne = eor&((~eor)+1);
        int onlyOne = 0;
        for (int i = 0; i < arr.length; i++) {
            if ((rightOne & arr[i]) !=0){
                onlyOne ^= arr[i];
            }
        }
        System.out.println(onlyOne +" "+ (eor^onlyOne));
        return new int[]{onlyOne,(eor^onlyOne)};
    }



    public static  void main(String[] args){
        int arr[] = {1,3,3,3,4,4,8,8,8,8,10,10};
        System.out.println(timeNum2(arr));
    }
}
