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

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

//Java: 有效的括号
public class P20ValidParentheses {
  public static void main(String[] args) {
    String str = "()";
    String str1 = "()[]{}";
    String str2 = "([)]";
    String str3 = "{[]}";
    String str4 = "((";
    int len = 30;
    System.out.println(new Solution().isValid(str4));
  }


  static class Solution {
    public boolean isValid(String s) {
      if ((s.length() & 1) == 1) {
        return false;
      }
      //()[]{}
      Map<Character, Character> map = new HashMap<Character, Character>(4){{
        put('(',')');put('[',']');put('{','}');
      }};
      Stack<Character> sk = new Stack<>();
      for (int i = 0; i < s.length(); i++) {
        char c = s.charAt(i);
        if (c == '(' || c == '[' || c == '{'){
          sk.push(c);
        }else{
          if (!sk.isEmpty() &&map.get(sk.pop())== c){
            continue;
          }else{
            return  false;
          }
        }
      }
      return  sk.size() == 0;
    }
  }
}