package cn.wr.algro.others;

/**
 * @author : WangRui
 * @date : 2023/2/2
 */

public class Test11 {

    public TreeNode createTree() {

        TreeNode A = new TreeNode("A");
        TreeNode B = new TreeNode("B");
        TreeNode C = new TreeNode("C");
        TreeNode D = new TreeNode("D");
        TreeNode E = new TreeNode("E");
        TreeNode F = new TreeNode("F");
        TreeNode G = new TreeNode("G");
        TreeNode H = new TreeNode("H");
        A.left = B;
        A.right = C;
        B.left = D;
        B.right = E;
        C.left = F;
        C.right = G;
        E.right = H;
        return A;
    }

    // 前序遍历
    // 先访问根节点--> 根的左子树 -->根的右子树 访问即为打印 遇到null 返回
    void preOrderTraversal(TreeNode root){
        if(root == null) {
            return;
        }
        System.out.print(root.value+" ");
        preOrderTraversal(root.left);
        preOrderTraversal(root.right);
    }

    // 中序遍历
    // 先访问根的左子树 --> 根节点 --> 根的右子树 遇到null 返回
    void midOrderTraversal(TreeNode root){
        if (root == null){
            return;
        }
        midOrderTraversal(root.left);
        System.out.println(root.value+"");
        midOrderTraversal(root.right);
    }

    // 后续遍历
    // 先访问根的左子树 --> 根的右子树 --> 根节点 遇到null 返回
    void postOrderTraversal(TreeNode root){
        if (root == null){
            return;
        }
        postOrderTraversal(root.left);
        postOrderTraversal(root.right);
        System.out.println(root.value+"");
    }


    public static void main(String[] args) {

        Test11 test11 = new Test11();
        TreeNode tree = test11.createTree();

        // 前序遍历
        test11.preOrderTraversal(tree);

        // 中序遍历
        test11.midOrderTraversal(tree);

        // 后序遍历
        test11.postOrderTraversal(tree);
    }
}
