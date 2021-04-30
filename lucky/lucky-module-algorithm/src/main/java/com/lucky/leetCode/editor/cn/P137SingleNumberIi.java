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
import java.util.Map;

/**
 * @author Loki
 * @Data
 */
public class P137SingleNumberIi {
    public static void main(String[] args) {
        int[] arr = {4,3,3,3,2,2,2,5,5,5};
        System.out.println(new Solution().singleNumbers(arr));
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
    public int test1(int[] nums) {
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