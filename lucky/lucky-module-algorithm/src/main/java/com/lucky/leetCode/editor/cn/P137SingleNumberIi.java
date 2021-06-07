//给你一个整数数组 nums ，除某个元素仅出现 一次 外，其余每个元素都恰出现 三次 。请你找出并返回那个只出现了一次的元素。
//
//
//
// 示例 1：
//
//
//输入：nums = [2,2,3,2]
//输出：3
//
//
// 示例 2：
//
//
//输入：nums = [0,1,0,1,0,1,99]
//输出：99
//
//
//
//
// 提示：
//
//
// 1 <= nums.length <= 3 * 104
// -231 <= nums[i] <= 231 - 1
// nums 中，除某个元素仅出现 一次 外，其余每个元素都恰出现 三次
//
//
//
//
// 进阶：你的算法应该具有线性时间复杂度。 你可以不使用额外空间来实现吗？
// Related Topics 位运算
// 👍 647 👎 0

package com.lucky.leetCode.editor.cn;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Loki
 * @Data
 */
//Java: 只出现一次的数字 II
public class P137SingleNumberIi {
    public static void main(String[] args) {
        //对数器
        System.out.println("开始！");
        int num = 100_0000;
        for (int i = 0; i <num ; i++) {
            // 个数
            int g = 50;
            // 随机出现K次的数 K一定比M小
            int k = 1;
            // 随机出现M次的数
            int M = 3;
            // 出现值的范围 range
            int range = 200;
            int[] ints = randomArray(g,range,k,M);
            int i1 = new Solution().singleNumbers(ints);
            int i2 = test1(ints);
            if (i1 != i2){
                System.out.println("失败了");
            }

        }
        System.out.println("结束");
    }

    public static int[] randomArray(int g,int range,int k,int M){
        int numKinds = (int) (Math.random()*g)+2;
        int[] arr = new int[k+(numKinds-1)*M];
        int Ks =  (int)(Math.random()*range);
        int index = 0;
        arr[index] =Ks;
        numKinds --;
        HashSet<Integer> set =  new HashSet<>();
        set.add(Ks);
        while (numKinds!=0){
            int Km =  0;
            do {
                Km =  (int)(Math.random()*range);
            }while (set.contains(Km));
            set.add(Km);
            numKinds --;
            for (int i = 0; i <M ; i++) {
                arr[++index] =Km;
            }
        }
        return arr;
    }

    static class Solution {
        /**
         * 保证一个数出现了 1 一次 其他数出现了 s 次;
         * @param nums
         * @return
         */
        int s = 3;
        public int singleNumbers(int[] nums) {
            int[] arr = new int[32];
            for (int num : nums) {
                for (int i = 0; i < arr.length-1; i++) {
                    //  ((num>>i)&1) != 0  表示 num 二进制数在 i 这个位置是1
                    arr[i] += ((num>>i)& 1) ;
                }
            }
            int ans = 0;
            for (int i = 0; i < arr.length-1; i++) {
                // 在i位置的的这个数量一定是s的整数倍。 那么需要查找k这个数 在i位置上一定不含1。
                if (arr[i] % s != 0){
                    ans |= (1<<i);
                }
            }
            return ans;
        }
    }


    /**
     * @param nums
     * @return
     */
    public static int test1(int[] nums) {
        Map<Integer, Integer> map = new HashMap<>(16);
        for (int num : nums) {
            if (map.containsKey(num)) {
                map.put(num, map.get(num) + 1);
            } else {
                map.put(num, 1);
            }
        }
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                return entry.getKey();
            }
        }
        return -1;
    }

}