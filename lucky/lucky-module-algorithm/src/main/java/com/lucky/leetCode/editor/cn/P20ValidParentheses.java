//给定一个只包括 '('，')'，'{'，'}'，'['，']' 的字符串 s ，判断字符串是否有效。 
//
// 有效字符串需满足： 
//
// 
// 左括号必须用相同类型的右括号闭合。 
// 左括号必须以正确的顺序闭合。 
// 
//
// 
//
// 示例 1： 
//
// 
//输入：s = "()"
//输出：true
// 
//
// 示例 2： 
//
// 
//输入：s = "()[]{}"
//输出：true
// 
//
// 示例 3： 
//
// 
//输入：s = "(]"
//输出：false
// 
//
// 示例 4： 
//
// 
//输入：s = "([)]"
//输出：false
// 
//
// 示例 5： 
//
// 
//输入：s = "{[]}"
//输出：true 
//
// 
//
// 提示： 
//
// 
// 1 <= s.length <= 104 
// s 仅由括号 '()[]{}' 组成 
// 
// Related Topics 栈 字符串 
// 👍 2260 👎 0

package com.lucky.leetCode.editor.cn;

import java.util.Stack;

public class P20ValidParentheses {
  public static void main(String[] args) {
    String str = "()";
    String str1 = "()[]{}";
    String str2 = "([)]";
    String str3 = "{[]}";
    boolean valid = new Solution().isValid(str);
    System.out.println(valid);
  }

  static class Solution {
    public boolean isValid(String s) {
      if ((s.length() & 1) == 1) {
        return false;
      }
      for (int i = s.length() - 1; i >= 0; i--) {
        Stack<Character> sk = new Stack<>();
        char c = s.charAt(i);
      }
      return true;
    }
  }
}