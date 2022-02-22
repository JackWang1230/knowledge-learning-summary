package cn.wr.algro.dynamic;

/**
 * @author RWang
 * @Date 2022/2/21
 */

/**
 * 这个递推方程的意思是，在求以ai为末元素的最长递增子序列时，找到所有序号在L前面且小于ai的元素aj，
 * 即j<i且aj<ai。如果这样的元素存在，那么对所有aj,都有一个以aj为末元素的最长递增子序列的长度f(j)，
 * 把其中最大的f(j)选出来，那么f(i)就等于最大的f(j)加上1，即以ai为末元素的最长递增子序列，
 * 等于以使f(j)最大的那个aj为末元素的递增子序列最末再加上ai；如果这样的元素不存在，
 * 那么ai自身构成一个长度为1的以ai为末元素的递增子序列。
 *
 *    L(j)={max(L(i))+1,i<j,a[i]<a[j]}
 *
 *
 */
public class Maxlength {

    public static void main(String[] args) {


        int[] L = {5,6,7,1,2,8};
        int n =L.length;
        // 用来存储最长子序列的长度
        int[] f = new int[n];

        f[0] = 1;
        for (int i = 1; i < n; i++) {
            f[i]=1;
            for (int j = 0; j < i; j++) {
                if (L[j]<L[i] && f[j]>f[i]-1)
                    f[i] = f[j]+1;
            }
        }

        // 状态的定义:  如何求出F1-FN中 最大子序列的长度 是 核心
        // 状态转移方程的定义： 1）针对求最大的F[k]的长度 也就是求F[k-1]项子序列的长度，+1
        // 经过上述的转换，对于前k个项的最大长度 也就是前k-1项的最大子序列长度 +1




    }
}
