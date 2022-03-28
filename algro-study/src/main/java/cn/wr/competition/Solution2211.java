package cn.wr.competition;

/**
 * @author RWang
 * @Date 2022/3/24
 */

/**
 * 输入：directions = "RLRSLL"
 * 输出：5
 * 解释：
 * 将会在道路上发生的碰撞列出如下：
 * - 车 0 和车 1 会互相碰撞。由于它们按相反方向移动，碰撞数量变为 0 + 2 = 2 。
 * - 车 2 和车 3 会互相碰撞。由于 3 是静止的，碰撞数量变为 2 + 1 = 3 。
 * - 车 3 和车 4 会互相碰撞。由于 3 是静止的，碰撞数量变为 3 + 1 = 4 。
 * - 车 4 和车 5 会互相碰撞。在车 4 和车 3 碰撞之后，车 4 会待在碰撞位置，接着和车 5 碰撞。碰撞数量变为 4 + 1 = 5 。
 * 因此，将会在道路上发生的碰撞总次数是 5 。
 *
 *  L,R->R,0
 *  R,L->S,2
 *  L,L->L,L,0
 *  L,S->L,S,0
 *  R,R->R,R,0
 *  R,S->S,1
 *  S,L->S,1
 *  S,R->S,R,0
 *
 */
public class Solution2211 {

    public int countCollisions(String directions){

        int length = directions.length();


        return 0;

    }
}
