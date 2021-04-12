//编写一个函数来查找字符串数组中的最长公共前缀。 
//
// 如果不存在公共前缀，返回空字符串 ""。 
//
// 
//
// 示例 1： 
//
// 
//输入：strs = ["flower","flow","flight"]
//输出："fl"
// 
//
// 示例 2： 
//
// 
//输入：strs = ["dog","racecar","car"]
//输出：""
//解释：输入不存在公共前缀。 
//
// 
//
// 提示： 
//
// 
// 0 <= strs.length <= 200 
// 0 <= strs[i].length <= 200 
// strs[i] 仅由小写英文字母组成 
// 
// Related Topics 字符串 
// 👍 1513 👎 0

package com.lucky.leetCode.editor.cn;

public class P14LongestCommonPrefix {
  public static void main(String[] args) {
    String arr[] = {};
    String prefix = new P14LongestCommonPrefix().new Solution().longestCommonPrefix(arr);
    System.out.println(prefix);

  }

  class Solution {
    public String longestCommonPrefix(String[] strs) {
      if (strs.length == 0) {
        return "";
      }
      if ("".equals(strs[0])) {
        return "";
      }
      String st = strs[0];
      for (int i = 1; i < strs.length; i++) {
        int j = 0;
        for (j = 0; j < strs[i].length() && j < st.length(); j++) {
          if (st.charAt(j) != strs[i].charAt(j)) {
            break;
          }
        }
        st = st.substring(0, j);
        if ("".equals(st)) {
          return st;
        }
      }
      return st;
    }
  }

}