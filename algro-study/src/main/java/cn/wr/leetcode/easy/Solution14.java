package cn.wr.leetcode.easy;

import java.util.Locale;

/**
 * @author : WangRui
 * @date : 2023/3/9
 */

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
// 1 <= strs.length <= 200
// 0 <= strs[i].length <= 200
// strs[i] 仅由小写英文字母组成
//
//
// Related Topics 字典树 字符串 👍 2671 👎 0
public class Solution14 {

    // 最长公共前缀。
    public String longestCommonPrefix(String[] strs) {

        // 思路 获取数组中字符串最短的字符串
        int minLength = strs[0].length();
        for (int i = 1; i < strs.length; i++) {
            if (strs[i].length()<minLength){
                minLength = strs[i].length();
            }
        }

        int index =0;
        boolean flag = true;
        for (int i = 0; i < minLength; i++) {
            // 获取第一个数组进行比较
            char c = strs[0].charAt(i);
            for (int j = 1; j < strs.length; j++) {
                char c1 = strs[j].charAt(i);
                if (c != c1){
                    flag = false;
                    break;
                }
            }
            if (!flag){
                break;
            }
            index ++;
        }
        if (index>0){
            return strs[0].substring(0,index);
        }
        return "";
    }


    public static void main(String[] args) throws InstantiationException, IllegalAccessException {

        String[] a = {"bc","abds","ads"};
        String s = Solution14.class.newInstance().longestCommonPrefix(a);
        System.out.println(s);
    }
}
