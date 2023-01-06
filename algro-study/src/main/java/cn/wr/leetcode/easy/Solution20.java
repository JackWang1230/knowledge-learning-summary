package cn.wr.leetcode.easy;

import java.util.Stack;

/**
 * @author : WangRui
 * @date : 2022/12/26
 *
 * 给定一个只包括 '('，')'，'{'，'}'，'['，']' 的字符串 s ，判断字符串是否有效。
 *
 * 有效字符串需满足：
 *
 * 左括号必须用相同类型的右括号闭合。
 * 左括号必须以正确的顺序闭合。
 * 每个右括号都有一个对应的相同类型的左括号。
 *
 * 示例 1：
 *
 * 输入：s = "()"
 * 输出：true
 * 示例 2：
 *
 * 输入：s = "()[]{}"
 * 输出：true
 * 示例 3：
 *
 * 输入：s = "(]"
 * 输出：false
 *
 */
public class Solution20 {

    public boolean isValid(String s){

        Stack<Character> stack = new Stack<>();
        char[] chars = s.toCharArray();
        for (char aChar : chars) {
            if (aChar=='('){
                stack.push(')');
            }else if (aChar == '{'){
                stack.push('}');
            }else if(aChar=='['){
                stack.push(']');
            }else if (stack.isEmpty() || aChar != stack.pop()){
                return false;
            }
        }
        return stack.isEmpty();

    }

    public static void main(String[] args) throws Exception {
        String s = "(]";

        boolean valid = Solution20.class.newInstance().isValid(s);

    }

}
