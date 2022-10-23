package cn.wr.huawei;

import java.util.Scanner;

/**
 *
 * 输入一个字符串仅包含大小写字母和数字
 *             求字符串中包含的最长的非严格递增连续数字序列长度
 *             比如：
 *                 12234属于非严格递增数字序列
 *             示例：
 *             输入
 *                 abc2234019A334bc
 *             输出
 *                 4
 *             说明：
 *                 2234为最长的非严格递增连续数字序列，所以长度为4
 *
 *                 aaaaaa44ko543j123j7345677781
 *                 aaaaa34567778a44ko543j123j71
 *                 345678a44ko543j123j7134567778aa
 *
 * @author : WangRui
 * @date : 2022/10/12
 */

public class Solution11 {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        char[] chars = s.toCharArray();

        int maxLen =0;
        int currentLen = 0;
        char lastValue = 0;
        for (int i = 0; i < chars.length; i++) {
            // 先判断是不是数字
            if (Character.isDigit(chars[i])){
                // 当前长度为0
                if (currentLen==0){
                    currentLen++;
                    //  aaaaaa44ko543j123j7345677781
                }else if(chars[i]>=lastValue){
                    // 如果下一位也是数字 并且大于前一位
                    currentLen++;
                }else {
                    // 当前数字比上一位要小 此时需要停止
                    // 比较当前的长度和 最大的长度谁最大
                    if(currentLen>maxLen){
                        maxLen= currentLen;
                    }
                    // 重置当前长度
                    currentLen =1;
                }
                // 345678ad44ko543j123j7134567778aa
                lastValue = chars[i];
            }else {
                // 如果不是数字
                lastValue = 0;
                if (currentLen>maxLen){
                    maxLen = currentLen;
                }
                currentLen = 0;
            }
        }
        if (currentLen>maxLen){
            maxLen= currentLen;
        }
        System.out.println(maxLen);
    }
}
