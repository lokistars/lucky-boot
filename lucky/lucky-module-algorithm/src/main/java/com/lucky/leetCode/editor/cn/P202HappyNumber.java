//编写一个算法来判断一个数 n 是不是快乐数。 
//
// 「快乐数」定义为： 
//
// 
// 对于一个正整数，每一次将该数替换为它每个位置上的数字的平方和。 
// 然后重复这个过程直到这个数变为 1，也可能是 无限循环 但始终变不到 1。 
// 如果 可以变为 1，那么这个数就是快乐数。 
// 
//
// 如果 n 是快乐数就返回 true ；不是，则返回 false 。 
//
// 
//
// 示例 1： 
//
// 
//输入：19
//输出：true
//解释：
//12 + 92 = 82
//82 + 22 = 68
//62 + 82 = 100
//12 + 02 + 02 = 1
// 
//
// 示例 2： 
//
// 
//输入：n = 2
//输出：false
// 
//
// 
//
// 提示： 
//
// 
// 1 <= n <= 231 - 1 
// 
// Related Topics 哈希表 数学 
// 👍 614 👎 0

package com.lucky.leetCode.editor.cn;

import java.util.ArrayList;
import java.util.List;

//Java：快乐数
public class P202HappyNumber {
    public static void main(String[] args) {
        System.out.println(new Solution().isHappy(3));
    }

    static class Solution {
        public boolean isHappy(int n) {
            List<Integer> list = new ArrayList<>();
            int ans = 0;
            while (ans != 1 || n != 0) {
                int tmp = n % 10;
                tmp = tmp * tmp;
                n = n / 10;
                ans = ans + tmp;
                if (n == 0 && ans != 1) {
                    if (list.contains(ans)) {
                        return false;
                    } else {
                        list.add(ans);
                        n = ans;
                        ans = 0;
                    }
                }
            }
            return true;
        }
    }
}