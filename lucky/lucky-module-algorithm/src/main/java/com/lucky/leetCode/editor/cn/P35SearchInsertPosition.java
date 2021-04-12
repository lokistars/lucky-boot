//给定一个排序数组和一个目标值，在数组中找到目标值，并返回其索引。如果目标值不存在于数组中，返回它将会被按顺序插入的位置。 
//
// 你可以假设数组中无重复元素。 
//
// 示例 1: 
//
// 输入: [1,3,5,6], 5
//输出: 2
// 
//
// 示例 2: 
//
// 输入: [1,3,5,6], 2
//输出: 1
// 
//
// 示例 3: 
//
// 输入: [1,3,5,6], 7
//输出: 4
// 
//
// 示例 4: 
//
// 输入: [1,3,5,6], 0
//输出: 0
// 
// Related Topics 数组 二分查找 
// 👍 876 👎 0

package com.lucky.leetCode.editor.cn;

public class P35SearchInsertPosition {
    public static void main(String[] args) {
        int[] arr ={1,3,5};
        int i = new Solution().searchInsert(arr, 3);
        System.out.println(i);
    }

    static class Solution {
        public int searchInsert(int[] nums, int target) {
            int left = 0;
            int m = 0;
            int right = nums.length -1;
            if (target <= nums[0]){
                return 0;
            }
            if (target > nums[nums.length-1]){
                return nums.length;
            }
            while (left<right){
                m = left + ((right -left)>>1);
                if (nums[m] == target){
                    return m;
                }else if (nums[m] >target){
                    right = m;
                }else{
                    left = m +1 ;
                }
            }
            return left;
        }
    }
}