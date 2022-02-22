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


        System.out.println("hello world");
    }
}
