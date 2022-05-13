package cn.wr.algro.arrays;

/**
 * @author RWang
 * @Date 2022/4/20
 *
 * 双指针解题法
 */

public class Solution26 {
    // 输入：nums = [0,0,1,1,1,2,2,3,3,4]
    //输出：5, nums = [0,1,2,3,4]
    //
    public int removeDuplicates(int[] nums) {

        if (nums.length==1){
            return 1;
        }
        int fastIndex;
        int slowIndex=0;
        for (fastIndex=0; fastIndex < nums.length; fastIndex++) {

            if (nums[slowIndex]!=nums[fastIndex]){
                slowIndex++;
                nums[slowIndex]=nums[fastIndex];
            }
        }
        return slowIndex+1;

    }

    public static void main(String[] args) {
        int[] nums={0,0,1,1,1,2,2,3,3,4};
        Solution26 solution26 = new Solution26();
        solution26.removeDuplicates(nums);

    }
}
