package cn.wr.huawei;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 *5
 * 88a-fFAD#A-Fa&k4ppP
 *
 * 88a-FFAD#-AFa&k-4ppp
 *
 *
 * 实现逻辑  基于下标一次substring 然后将里面的数据比较大小写
 *
 * 字符串分割
 * @author : WangRui
 * @date : 2022/10/11
 */

public class Solution1 {



    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            int k = Integer.parseInt(sc.nextLine());
            String line = sc.nextLine();
            // 获取下标所在的位置
            int index = line.indexOf("-");
            // 第一个子串
            String first = line.substring(0, index);
            String replace = line.substring(index + 1).replaceAll("-", "");

            // 存储新生成的字符串
            List<String> list = new ArrayList<>();
            list.add(first);
            while (replace.length() >= k) {
                String sub = replace.substring(0, k);
                sub = convert(sub);
                list.add(sub);
                replace = replace.substring(k);
            }
            if (replace.length() > 0) {
                list.add(convert(replace));
            }
            System.out.println(String.join("-",list));

        }
        sc.close();
    }




    private static String convert(String sub) {
        // 统计小写字母个数
        int lowCount = 0;
        // 统计大写字母个数
        int upperCount = 0;
        for (int i = 0; i < sub.length(); i++) {
            char c = sub.charAt(i);
            if (Character.isLowerCase(c)) {
                lowCount++;
            } else if (Character.isUpperCase(c)) {
                upperCount++;
            }
        }
        if (lowCount > upperCount) {
            sub = sub.toLowerCase(Locale.ROOT);
        } else if (lowCount < upperCount) {
            sub = sub.toUpperCase(Locale.ROOT);
        }
        return sub;
    }


    private static String  convert11(String sub){

        // 需要先判断一下大写字母 和小写字母的个数
        int minCount =0;
        int maxCount = 0;
        char[] chars = sub.toCharArray();
        for (char aChar : chars) {
            if (Character.isLowerCase(aChar)){
                minCount +=minCount;
            }
            if (Character.isUpperCase(aChar)){
                maxCount += maxCount;
            }
        }
        if (maxCount>minCount){
            return sub.toLowerCase(Locale.ROOT);
        } else if (maxCount < minCount){
            return sub.toUpperCase(Locale.ROOT);
        }
        return sub;
    }

}
