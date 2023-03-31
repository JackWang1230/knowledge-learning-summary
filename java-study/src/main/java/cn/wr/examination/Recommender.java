package cn.wr.examination;

/**
 * @author : WangRui
 * @date : 2023/3/31
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Recommender {

    private static final int NUM_RECOMMENDATIONS = 3;

    public static void main(String[] args) throws IOException {
        // 从CSV文件中读取销售数据
        // List<Sale> sales = readSalesFromFile("sales.csv");
        List<Sale> sales = readSalesFromFile("/Users/wangrui/Documents/coding/knowledge-learning-summary/java-study/src/main/resources/recommend/sales.csv");

        // 构建用户购买记录和商品销售记录
        Map<String, Set<String>> userPurchases = new HashMap<>();
        Map<String, Set<String>> productSales = new HashMap<>();
        for (Sale sale : sales) {
            userPurchases.computeIfAbsent(sale.getUserId(), k -> new HashSet<>()).add(sale.getProductId());
            productSales.computeIfAbsent(sale.getProductId(), k -> new HashSet<>()).add(sale.getUserId());
        }

        // 计算相似度矩阵
        Map<String, Map<String, Double>> similarityMatrix = new HashMap<>();
        for (String userId : userPurchases.keySet()) {
            Map<String, Double> userSimilarities = new HashMap<>();
            for (String productId : productSales.keySet()) {
                if (!userPurchases.get(userId).contains(productId)) {
                    Set<String> commonUsers = new HashSet<>(productSales.get(productId));
                    commonUsers.retainAll(userPurchases.get(userId));
                    double similarity = (double) commonUsers.size() / (double) productSales.get(productId).size();
                    userSimilarities.put(productId, similarity);
                }
            }
            similarityMatrix.put(userId, userSimilarities);
        }

        // 对于每个用户，根据相似度矩阵推荐前N个相似商品
        for (String userId : userPurchases.keySet()) {
            Set<String> purchasedProducts = userPurchases.get(userId);
            Map<String, Double> userSimilarities = similarityMatrix.get(userId);

            List<String> recommendations = userSimilarities.entrySet()
                    .stream()
                    .filter(entry -> !purchasedProducts.contains(entry.getKey()))
                    .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                    .limit(NUM_RECOMMENDATIONS)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            System.out.println("Recommendations for user " + userId + ": " + recommendations);
        }
    }

    private static List<Sale> readSalesFromFile(String fileName) throws IOException {
        List<Sale> sales = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                sales.add(new Sale(fields[0], fields[1], fields[2]));
            }
        }
        return sales;
    }

    private static class Sale {
        private final String userId;
        private final String productId;
        private final String purchaseTime;

        public Sale(String userId, String productId, String purchaseTime) {
            this.userId = userId;
            this.productId = productId;
            this.purchaseTime = purchaseTime;
        }

        public String getUserId() {
            return userId;
        }

        public String getProductId() {
            return productId;
        }

        public String getPurchaseTime() {
            return purchaseTime ;}}}


