package cn.wr.leetcode.medium;


/**
 * 盛最多水容器
 *
 * @author : WangRui
 * @date : 2024/1/17
 */

public class Solution11 {

    public int maxArea(int[] height) {

        // 输入：[1,8,6,2,5,4,8,3,7]
        // 输出：49
        // 此处通过双指针解决比较合理
        int res = 0;
        int i = 0;
        int j = height.length - 1;
        while (i < j) {
            int area = (j - i) * Math.min(height[i], height[j]);
            res = Math.max(res, area);
            if (height[i] < height[j]) {
                i++;
            } else {
                j--;
            }
        }
        return res;
    }

    public static void main(String[] args) {
        int[] height = {1,8,6,2,5,4,8,3,7};
        try {
            int res = Solution11.class.newInstance().maxArea(height);
            System.out.println(res);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
