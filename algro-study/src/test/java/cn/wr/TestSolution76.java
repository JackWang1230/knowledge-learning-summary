package cn.wr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author RWang
 * @Date 2022/3/31
 *
 *  * 给你一个字符串 s 、一个字符串 t 。返回 s 中涵盖 t 所有字符的最小子串。如果 s 中不存在涵盖 t 所有字符的子串，则返回空字符串 "" 。
 *  *
 *  *
 *  * 输入：s = "ADOBECODEBANC", t = "ABC"
 *  * 输出："BANC"
 *  *
 *  *
 *  * 输入：s = "a", t = "a"
 *  * 输出："a"
 *  *
 *  *
 *  *
 *  * 输入: s = "a", t = "aa"
 *  * 输出: ""
 *  * 解释: t 中两个字符 'a' 均应包含在 s 的子串中，
 *  * 因此没有符合条件的子字符串，返回空字符串。
 */

public class TestSolution76 {


    public String minWindow(String s,String t){

        // 排除特殊可能性
        if (s==null || t==null || s.length()==0 || t.length()==0
        || s.length()<t.length()){
            return "";
        }
        // 将s和t的数据基于map存储起来
        Map<Character, Integer> needs = new HashMap<>();
        char[] chars = t.toCharArray();
        for (char aChar : chars) {
            Integer num = needs.getOrDefault(aChar, 0);
            needs.put(aChar,num+1);
        }
        // 引入双指针处理
        int sLength = s.length();
        int tLength = t.length();
        int left =0;
        int result = Integer.MAX_VALUE;
        int minLeft =0;
        Map<Character,Integer> maps = new HashMap<>();
        for (int right = 0; right < sLength; right++) {
            // 创建窗口长度
            char c = s.charAt(right);
            Integer value = maps.getOrDefault(c, 0);
            maps.put(c,value+1);
            // 如果不满足needs中的需求，移动right指针直到满足
            // 1.窗口长度首先必须大于等于 target长度
            // 2.其次需要满足target中有的数组在 s中都包含
            while (right-left>=tLength && minWindowHelp(needs,maps)){
                // s = "ADOB ECODEBA NC", t = "ABC"
                int curLeftRight = right - left;
                if (curLeftRight < result) {
                    //可能有很多符合条件的，比较出left、right间隔最小的
                    minLeft = left;
                    result = curLeftRight;
                }
                char c1 = s.charAt(left);
                // 因为每次都删除一个元素
                // 分为两种情况 如果该元素大于1 则逻辑上是value-1,否则直接删除
                Integer integer = maps.get(c1);

                if (integer==1){
                    maps.remove(c1);
                }else {
                    maps.put(c1,integer-1);
                }
                left++;
            }

        }
        return result==Integer.MAX_VALUE?"":s.substring(minLeft,minLeft+result);

    }


    public boolean minWindowHelp(Map<Character,Integer> needs,Map<Character,Integer> maps){

        if (maps.size()<needs.size()){
            return false;
        }
        // maps中的key必须要都在needs中，并且 value要大于等于 needs中的value即可
        Set<Map.Entry<Character, Integer>> entries = needs.entrySet();
        for (Map.Entry<Character, Integer> entry : entries) {
            Character key = entry.getKey();
            Integer value = entry.getValue();
            if (!maps.containsKey(key)|| maps.get(key)<value){
                return false;
            }
        }
        return true;


    }
}
