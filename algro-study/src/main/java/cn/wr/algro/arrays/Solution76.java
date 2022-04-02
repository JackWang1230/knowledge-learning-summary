package cn.wr.algro.arrays;

import java.util.HashMap;
import java.util.Map;

/**
 * @author RWang
 * @Date 2022/3/31
 *
 * 给你一个字符串 s 、一个字符串 t 。返回 s 中涵盖 t 所有字符的最小子串。如果 s 中不存在涵盖 t 所有字符的子串，则返回空字符串 "" 。
 *
 *
 * 输入：s = "ADOBECODEBANC", t = "ABC"
 * 输出："BANC"
 *
 *
 * 输入：s = "a", t = "a"
 * 输出："a"
 *
 *
 *
 * 输入: s = "a", t = "aa"
 * 输出: ""
 * 解释: t 中两个字符 'a' 均应包含在 s 的子串中，
 * 因此没有符合条件的子字符串，返回空字符串。
 *
 */

public class Solution76 {

    public String minWindow(String s, String target) {

        if (s == null || target == null || s.length() == 0
                || target.length() == 0 || target.length() > s.length()) {
            return "";
        }
        Map<Character, Integer> needs = new HashMap<>();
        for (char ch : target.toCharArray()) {
            int nums = needs.getOrDefault(ch, 0);
            needs.put(ch, nums + 1);
        }
        int sLength = s.length();
        int tLength = target.length();
        int left = 0;//左指针
        int right = 0;//右指针
        int resLeftRight = Integer.MAX_VALUE;//保存最小的left、right的间隔
        int minLeft = 0;//保存间隔最小的符合条件的left
        Map<Character, Integer> windows = new HashMap<>();
        while (right < sLength) {
            char curChar = s.charAt(right);
            int curNum = windows.getOrDefault(curChar, 0);
            windows.put(curChar, curNum + 1);
            right++;
            while (right - left >= tLength && minWindowHelp(windows, needs)) {
                int curLeftRight = right - left;
                if (curLeftRight < resLeftRight) {
                    //可能有很多符合条件的，比较出left、right间隔最小的
                    minLeft = left;
                    resLeftRight = curLeftRight;
                }
                char leftChar = s.charAt(left);
                int leftCharNum = windows.get(leftChar);
                if (leftCharNum == 1) {
                    windows.remove(leftChar);
                } else {
                    windows.put(leftChar, leftCharNum - 1);
                }
                left++;
            }
        }
        return resLeftRight == Integer.MAX_VALUE ? ""
                : s.substring(minLeft, minLeft + resLeftRight);

    }

    public boolean minWindowHelp(Map<Character, Integer> windows, Map<Character, Integer> needs) {
        if (windows.size() < needs.size()) {
            return false;
        }
        for (Map.Entry<Character, Integer> entry : needs.entrySet()) {
            Character key = entry.getKey();
            int num = entry.getValue();
            if (!windows.containsKey(key) || windows.get(key) < num) {
                return false;
            }
        }
        return true;
    }


    public static void main(String[] args) {
        Map<Character, Integer> needs = new HashMap<>();

        String t ="aa";
        char[] chars = t.toCharArray();
        for (char aChar : chars) {
            int nums = needs.getOrDefault(aChar,0);
            needs.put(aChar,nums+1);
        }
        System.out.println("dd");
    }
}
