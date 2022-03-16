package cn.wr.algro;

import java.util.Arrays;

/**
 * @author RWang
 * @Date 2022/3/7
 *
 *
 * Input: nums = [1,4,25,10,25], k = 2
 * Output: 5
 * Explanation: The two unique positive integers that do not appear in nums which we append are 2 and 3.
 * The resulting sum of nums is 1 + 4 + 25 + 10 + 25 + 2 + 3 = 70, which is the minimum.
 * The sum of the two integers appended is 2 + 3 = 5, so we return 5.
 *
 *
 * Input: nums = [5,6], k = 6
 * Output: 25
 * Explanation: The six unique positive integers that do not appear in nums which we append are 1, 2, 3, 4, 7, and 8.
 * The resulting sum of nums is 5 + 6 + 1 + 2 + 3 + 4 + 7 + 8 = 36, which is the minimum.
 * The sum of the six integers appended is 1 + 2 + 3 + 4 + 7 + 8 = 25, so we return 25.
 *
 *
 * long hi = Math.min(num - 1, lo + k - 1);
 *                 int cnt = (int)(hi - lo + 1);
 *                 ans += (lo + hi) * cnt / 2;
 *                 k -= cnt;
 *
 */

public class Solution2195 {

    public long minimalKSum(int[] nums, int k) {

        Arrays.sort(nums);
        //  针对k的值进行比较
        // nums的最小值 到1之间的长度 和 k 到 1 之间的距离
        // 如果nums到1的长度大于 k-1 之间的距离 那么直接返回
        // 否则 取出

        long ans = 0, lo = 1;
        for (int num : nums) {
            if (num > lo) {
                long hi = Math.min(num - 1, lo + k - 1);
                int cnt = (int)(hi - lo + 1);
                ans += (lo + hi) * cnt / 2;
                k -= cnt;
                if (k == 0) {
                    return ans;
                }
            }
            lo = num + 1;
        }
        if (k > 0) {
            ans += (lo + lo + k - 1) * k / 2;
        }
        System.out.println(ans);
        return ans;
    }



    public long minimalKSum1(int[] nums,int k){

        return 1;
    }


    public static void main(String[] args) {
        int[] nums = {5,6};
        int k =6;
        Solution2195 solution2195 = new Solution2195();
        solution2195.minimalKSum(nums,k);

    }
}
