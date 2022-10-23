package cn.wr;

/**
 * @author : WangRui
 * @date : 2022/1/12
 */

public class AlgroHelloWorld {


    public int l(int[] nums,int i){

        if (i == nums.length-1){
            return 1;
        }
        int maxLength =1;
        for (int j = i; j < nums.length; j++) {
                if (nums[j]>nums[i]){
                   maxLength =Math.max(maxLength,l(nums,j)+1);
                }
        }
        return maxLength;
    }


    public static void main(String[] args) {

        int[] m = {1,3,4,2,5,6,98,9};

        int value=1;
        for (int i = 0; i < m.length; i++) {
            AlgroHelloWorld algroHelloWorld = new AlgroHelloWorld();
            value=Math.max(value,algroHelloWorld.l(m,i));
        }
        System.out.println(value);


        String a = "sdsdsds";
        String substring = a.substring(0,2);
        System.out.println(substring);


        System.out.println("hello world");


        String aaa = "dsds";

        char c = aaa.charAt(0);
        System.out.println(c);

        int[] offsetArr = new int[55];
        offsetArr[0] = 1;
        offsetArr[1] = 2;
        offsetArr[2] = 4;
        for (int i = 3; i < 55; i++) {
            offsetArr[i] = offsetArr[i - 1] + offsetArr[i - 2] + offsetArr[i - 3];
        }
        int css =offsetArr[40];
        System.out.println(css);
        char aaaa = 'a';
        char aaaaaaa = 'z';
        System.out.println("ddd");
        char aaas = 0;

       String aaaaaa= "s000";
        char[] chars = aaaaaa.toCharArray();
        for (int i = 0; i < chars.length; i++) {

            if (Character.isDigit(chars[i])){
                System.out.println(chars[i]);
            }
        }
        System.out.println("d");

        // int as = -0014;
        int as = Integer.parseInt("0014");
        int cda = 13;
        int cds = as+cda;
        System.out.println(cds);
    }
}
