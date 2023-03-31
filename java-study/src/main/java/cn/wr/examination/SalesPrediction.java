package cn.wr.examination;

/**
 * @author : WangRui
 * @date : 2023/3/31
 */

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SalesPrediction {
    public static void main(String[] args) {
        // 原始销售数据
        List<SalesData> salesDataList = new ArrayList<>();
        salesDataList.add(new SalesData(LocalDate.of(2021, 1, 1), 1000));
        salesDataList.add(new SalesData(LocalDate.of(2021, 2, 1), 1500));
        salesDataList.add(new SalesData(LocalDate.of(2021, 3, 1), 2000));
        salesDataList.add(new SalesData(LocalDate.of(2021, 4, 1), 3500));
        salesDataList.add(new SalesData(LocalDate.of(2021, 5, 1), 4000));

        // 构建预测模型
        SalesPredictor predictor = new SalesPredictor(salesDataList);

        // 预测下一个月销售额
        double prediction = predictor.predictNextMonthSales();
        System.out.println("预测下一个月销售额为：" + prediction);
    }
}

class SalesData {
    private LocalDate date;
    private double sales;

    public SalesData(LocalDate date, double sales) {
        this.date = date;
        this.sales = sales;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getSales() {
        return sales;
    }
}

class SalesPredictor {
    private List<SalesData> salesDataList;
    private int n;

    public SalesPredictor(List<SalesData> salesDataList) {
        this.salesDataList = salesDataList;
        this.n = salesDataList.size();
    }

    public double predictNextMonthSales() {
        double[] y = new double[n];
        double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            y[i] = salesDataList.get(i).getSales();
            x[i] = i + 1;
        }

        // 计算斜率和截距
        double sumX = 0.0, sumY = 0.0, sumXY = 0.0, sumXX = 0.0;
        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumXX += x[i] * x[i];
        }
        double slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;

        // 预测下一个月的销售额
        double nextMonthSales = slope * (n + 1) + intercept;
        return nextMonthSales;
    }
}

