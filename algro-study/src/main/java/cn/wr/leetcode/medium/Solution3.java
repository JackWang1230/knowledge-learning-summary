package cn.wr.leetcode.medium;

import java.util.HashSet;
import java.util.Set;

/**
 * @author : WangRui
 * @date : 2023/3/17
 */

//给定一个字符串 s ，请你找出其中不含有重复字符的 最长子串 的长度。
//
//
//
// 示例 1:
//
//
//输入: s = "abcabcbb"
//输出: 3
//解释: 因为无重复字符的最长子串是 "abc"，所以其长度为 3。
//
//
// 示例 2:
//
//
//输入: s = "bbbbb"
//输出: 1
//解释: 因为无重复字符的最长子串是 "b"，所以其长度为 1。
//
//
// 示例 3:
//
//
//输入: s = "pwwkew"
//输出: 3
//解释: 因为无重复字符的最长子串是 "wke"，所以其长度为 3。
//     请注意，你的答案必须是 子串 的长度，"pwke" 是一个子序列，不是子串。
//
//
//
//
// 提示：
//
//
// 0 <= s.length <= 5 * 10⁴
// s 由英文字母、数字、符号和空格组成
//
//
// Related Topics 哈希表 字符串 滑动窗口 👍 8884 👎 0
public class Solution3 {

    public int lengthOfLongestSubstring(String s) {
        int n = s.length();
        Set<Character> set = new HashSet<>();
        int ans = 0, i = 0, j = 0;
        while (i < n && j < n) {
            // try to extend the range [i, j]
            if (!set.contains(s.charAt(j))){
                set.add(s.charAt(j++));
                ans = Math.max(ans, j - i);
            }
            else {
                set.remove(s.charAt(i++));
            }
        }
        return ans;

    }


    public static void main(String[] args) {


        String s = "abcabcbb";
        Solution3 solution3 = new Solution3();
        int i = solution3.lengthOfLongestSubstring(s);
        System.out.println(i);
    }
}
