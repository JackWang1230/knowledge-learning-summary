package cn.wr.examination;

/**
 * @author : WangRui
 * @date : 2023/3/31
 */

import java.util.*;
import java.util.stream.Collectors;

public class EvaluationStatistics {
    public static void main(String[] args) {
        String data = "评价ID,用户ID,评价时间,评价内容,评价星级\n" +
                "1,001,2021-01-01,很好，下次还会购买,5\n" +
                "2,002,2021-01-02,质量不错，物美价廉,4\n" +
                "3,003,2021-01-03,发货速度很快，值得信赖,5\n" +
                "4,001,2021-01-04,产品质量不错，物有所值,4\n" +
                "5,002,2021-01-05,客服服务态度不好，需要改进,2\n";

        // 将数据转换成行列表
        List<String> lines = Arrays.asList(data.split("\n"));

        // 移除表头
        lines = lines.subList(1, lines.size());

        // 创建一个Map，用于存储每个用户的评价记录
        Map<String, List<Integer>> evaluationsByUser = new HashMap<>();

        // 遍历每一行数据，将评价星级加入对应用户的评价记录中
        for (String line : lines) {
            String[] values = line.split(",");
            String userId = values[1];
            int rating = Integer.parseInt(values[4]);
            evaluationsByUser.computeIfAbsent(userId, k -> new ArrayList<>()).add(rating);
        }

        // 计算所有用户的平均评价星级
        double overallAvgRating = evaluationsByUser.values().stream()
                .flatMapToInt(list -> list.stream().mapToInt(Integer::intValue))
                .average()
                .orElse(0);
        System.out.println("所有用户的平均评价星级为：" + overallAvgRating);

        // 计算每个用户的平均评价星级，并按用户ID排序输出
        evaluationsByUser.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    String userId = entry.getKey();
                    List<Integer> ratings = entry.getValue();
                    double avgRating = ratings.stream()
                            .mapToInt(Integer::intValue)
                            .average()
                            .orElse(0);
                    System.out.println("用户 " + userId + " 的平均评价星级为：" + avgRating);
                });
    }
}

