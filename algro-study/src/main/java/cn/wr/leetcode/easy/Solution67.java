package cn.wr.leetcode.easy;

/**
 * @author : WangRui
 * @date : 2023/3/9
 */

public class Solution67 {

    public String addBinary(String a, String b) {

        int length1 = a.length()-1;
        int length2 = b.length()-1;
        int carry = 0;
        StringBuilder sb = new StringBuilder();
        while (length1>=0 || length2>=0){
            int m = length1<0?0:a.charAt(length1)-'0';
            int n = length2<0?0:b.charAt(length2)-'0';
            // 1010 1011
            int sum = m+n+carry;
            sb.append(sum%2); //取余

            carry = sum/2; // 取整
            length1--;
            length2--;
        }
        if (carry!=0) sb.append(carry);
        return sb.reverse().toString();
    }

    public static void main(String[] args) {

        String a = "1011";
        String b ="1001";

        String s = new Solution67().addBinary(a, b);
        System.out.println(
                s
        );
    }
}
