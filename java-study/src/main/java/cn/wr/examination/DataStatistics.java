package cn.wr.examination;

/**
 * @author : WangRui
 * @date : 2023/3/31
 */

import java.util.HashMap;
import java.util.Map;

public class DataStatistics {

    public static void main(String[] args) {
        String[] rawData = {
                "1,001,2021-01-01,很好 下次还会购买,5",
                "2,002,2021-01-02,质量不错 物美价廉,4",
                "3,003,2021-01-03,发货速度很快 值得信赖,5",
                "4,001,2021-01-04,产品质量不错 物有所值,4",
                "5,002,2021-01-05,客服服务态度不好 需要改进,2"
        };

        // 用一个Map来存储每个用户的评价信息
        Map<String, UserEvaluation> userMap = new HashMap<>();
        for (String data : rawData) {
            String[] fields = data.split(",");
            String userId = fields[1];
            int star = Integer.parseInt(fields[4]);

            UserEvaluation userEvaluation = userMap.get(userId);
            if (userEvaluation == null) {
                userEvaluation = new UserEvaluation();
                userMap.put(userId, userEvaluation);
            }

            userEvaluation.totalStar += star;
            userEvaluation.totalCount++;
        }

        // 计算每个用户的平均评价星级和所有用户的平均评价星级
        double totalStar = 0.0;
        int totalCount = 0;
        for (UserEvaluation userEvaluation : userMap.values()) {
            double avgStar = (double) userEvaluation.totalStar / userEvaluation.totalCount;
            System.out.println("用户" + userEvaluation.userId + "的平均评价星级为：" + avgStar);
            totalStar += userEvaluation.totalStar;
            totalCount += userEvaluation.totalCount;
        }
        double avgStar = (double) totalStar / totalCount;
        System.out.println("所有用户的平均评价星级为：" + avgStar);
    }

    private static class UserEvaluation {
        public String userId;
        public int totalStar;
        public int totalCount;
    }
}

