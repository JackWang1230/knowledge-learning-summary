package cn.wr.leetcode.easy;

/**
 * @author : WangRui
 * @date : 2022/12/26
 */


import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 给定一个二叉树的根节点 root ，返回 它的 中序 遍历 。
 * <p>
 * 输入：root = [1,null,2,3]
 * 输出：[1,3,2]
 * <p>
 * 输入：root = []
 * 输出：[]
 * <p>
 * 输入：root = [1]
 * 输出：[1]
 */

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }

    TreeNode(int val) {
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

public class Solution92 {
    public List<Integer> inorderTraversal(TreeNode root){

        ArrayList<Integer> ans = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        while (root != null || !stack.isEmpty()){
            // 先根后左入栈
            while (root != null){
                stack.push(root);
                root = root.left;
            }
            //如果此时 root.left == null 为空了
            // 需要将栈中数据吐出来
            // 值取出来以后 如果 root
            root = stack.pop();
            ans.add(root.val);
            root = root.right;
        }
        return ans;

    }
}
