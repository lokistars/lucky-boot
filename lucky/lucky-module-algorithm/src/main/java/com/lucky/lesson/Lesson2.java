package com.lucky.lesson;

import java.util.*;

/**
 * 亦或运算
 * 相邻为0,不同为1 无进位相加
 * 0和任何数运算都是那个数本身
 * 同一个数相互运算 等于 0
 * 同样一批数不管什么样的运算顺序 结果都是一个
 * @author: Loki
 * @data: 2021-11-11 16:25
 **/
public class Lesson2 {

    /**
     * 不用额外的变量交换两个数的值
     */
    private static void swap(int[] arr ,int i,int j){
        arr[i] = arr[i]^arr[j];
        arr[j] = arr[i]^arr[j];
        arr[i] = arr[i]^arr[j];
    }

    /**
     *  一个数组中只有一种数出现了奇数次,其他数都出现了偶数次,找到这个数
     */
    private static int printTimesNum1(int[] arr){
        int eor = 0;
        for (int i : arr) {
            eor=eor^i;
        }
        return eor;
    }

    /**
     * 把一个整数的最右侧 1提取出来 a&(-a) -相反数 ~ 表示取反加 1
     *
     */
    private static void printTimesNum3(){
        int eor = 3;
        System.out.println(eor&(-eor));
    }

    /**
     * 一个数组中有两个数出现了奇数次
     * 既然是两个奇数那么最后可以得到 a!=b , a^b 不等于 0
     * 通过提取最右侧的1 来把数据分为两类数据, 一个是存在1 一个是不存在 1
     * 用一个数去亦或 这两类数据 就可以找到其中一个数
     */
    private static int[] printTimesNum2(int[] arr){
        int eor = 0;
        int eorN = 0;
        for (int i : arr) {
            eor ^=i;
        }
        int rightOne = eor&(-eor);
        for (int i : arr) {
            if ((i&rightOne)!=0){
                eorN ^= i;
            }
        }
        return new int[]{eorN,eor^eorN};
    }

    /**
     * 一个数组中有一种数出现了K次,其他数都出现了M次  M>1,k<M,找到k
     * 空间复杂度O(1) 时间复杂度 O(N)
     * 把每个数转换为32位的二进制数1存储到一个数组中,遍历这个数组 每个数如果不是m的倍数就可以找到k
     * | 或运算  两个位只要有一个为1，那么结果就是1
     */
    private static int printTimesNum3(int[] arr,int k,int m){
        int[] num = new int[32];
        for (int i : arr) {
            for (int j = 0; j < num.length; j++) {
                num[j] += (i >> j)&1;
            }
        }
        int eor = 0;
        for (int i = 0; i<num.length; i++){
            if (num[i] % m != 0){
                eor |= (1<<i);
            }
        }
        return eor;
    }

    private static int printTimesNum4(int[] arr,int k,int m){
        Map<Integer,Integer> map = new HashMap<>(16);
        for (int i : arr) {
            if (map.containsKey(i)){
                map.put(i, map.get(i)+1);
            }else{
                map.put(i,1);
            }
        }
        for (Integer integer : map.keySet()) {
            if (map.get(integer) == k){
                return integer;
            }
        }
        return -1;
    }

    /**
     * 随机数组
     * 总共有多少种数,数据范围
     *
     */
    private static int[] randomArray(int kinds,int range,int k,int m){
        // 获取一个随机数
        int randomNum = (int)(Math.random() * range);

        // 总数随机,最少两个
        kinds = (int) (Math.random() +kinds) +2;
        // 数组长度
        int len = k+ (kinds-1)*m;
        int[] arr = new int[len];
        //把出现了K次的数增加到数组中
        int index = 0;
        for(;index<k;index++){
            arr[index] = randomNum;
        }
        // 总数减一,k的数已经存好了
        kinds--;
        Set<Integer> set = new HashSet<>();
        set.add(randomNum);
        while (kinds != 0){
            // 获取随机数
            do {
                randomNum = (int)(Math.random() * range);
            }while (set.contains(randomNum));
            // 添加到数组中
            for (int i = 0; i < m; i++) {
                arr[index++] = randomNum;
            }
            kinds--;
        }
        return arr;


    }

    private static void randomD(){
        int kinds = 5;
        int range = 200;
        int testTime = 100000;
        int max = 9;
        for (int i = 0; i < testTime; i++) {
            // 生成有效的 k 和 m , 必须保证 m大于K
            int k = (int)(Math.random()*max )+1;
            int m = (int)(Math.random()*max )+1;
            k = Math.min(k,m);
            m = Math.max(k,m);
            if (k == m){
                m ++;
            }
            int[] arr = randomArray(kinds,range,k,m);
            int ans1 = printTimesNum3(arr,k,m);
            int ans2 = printTimesNum4(arr,k,m);
            if (ans1 != ans2){
                System.out.println("测试异常");
                System.out.println(ans1 + " " +ans2);
                break;
            }
        }
        System.out.println("end！！！");
    }

    public static void main(String[] args) {
        randomD();
    }
}
