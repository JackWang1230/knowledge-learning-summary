package cn.wr.algro;

/**
 * @author RWang
 * @Date 2022/1/13
 */

public class ReverseInt {

    public int reverse(int x) {
        long n = 0;
        while(x != 0) {
            n = n*10 + x%10;
            x = x/10;
        }
        return (int)n==n? (int)n:0;
    }

    public static void main(String[] args) {
        ReverseInt reverseInt = new ReverseInt();
        int reverse = reverseInt.reverse(10001);
        System.out.println(reverse);
    }
}
