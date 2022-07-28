package cn.wr.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class TestPolarDBBinlog {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestPolarDBBinlog.class);

    private static final String update = "{\"data\":[{\"id\":\"548152\",\"approval_number\":\"国妆特进字J20190153\",\"product_name\":\"歌薇颜色护理膏5MB（配合歌薇双氧水使用）\",\"spec\":null,\"product_unit\":\"Kao Germany GmbH\",\"product_addr\":\"Pfungstädter Strasse 92-100 64297 Darmstadt， Germany\",\"approve_date\":\"2019-01-03\",\"end_date\":\"4\",\"goods_type\":\"13\",\"domestic_foreign\":\"2\",\"relation_id\":\"212634\",\"cate_one\":null,\"cate_two\":null,\"cate_three\":null,\"cate_four\":null,\"cate_five\":null}],\"database\":\"cn_uniondrug_middleend_goodscenter\",\"es\":1603961174000,\"id\":8,\"isDdl\":false,\"mysqlType\":{\"id\":\"bigint(20)\",\"approval_number\":\"varchar(255)\",\"product_name\":\"varchar(512)\",\"spec\":\"text\",\"product_unit\":\"varchar(512)\",\"product_addr\":\"text\",\"approve_date\":\"varchar(255)\",\"end_date\":\"varchar(255)\",\"goods_type\":\"int(20)\",\"domestic_foreign\":\"int(20)\",\"relation_id\":\"bigint(20)\",\"cate_one\":\"varchar(255)\",\"cate_two\":\"varchar(255)\",\"cate_three\":\"varchar(255)\",\"cate_four\":\"varchar(255)\",\"cate_five\":\"varchar(255)\"},\"old\":[{\"approval_number\":\"国药准字Z14020687\",\"product_name\":\"龟龄集3\",\"spec\":\"每粒装0.3g\",\"product_unit\":\"山西广誉远国药有限公司\",\"product_addr\":\"山西省晋中市太谷县广誉远路1号\",\"approve_date\":\"2015-09-28\",\"end_date\":\"2015-09-28\",\"goods_type\":\"11\",\"domestic_foreign\":\"1\",\"relation_id\":\"1\",\"cate_one\":\"Z\",\"cate_two\":\"ZA\",\"cate_three\":\"ZA09\",\"cate_four\":\"ZA09D\",\"cate_five\":\"ZA09DC\"}],\"pkNames\":[\"id\"],\"sql\":\"\",\"sqlType\":{\"id\":-5,\"approval_number\":12,\"product_name\":12,\"spec\":-4,\"product_unit\":12,\"product_addr\":2005,\"approve_date\":12,\"end_date\":12,\"goods_type\":4,\"domestic_foreign\":4,\"relation_id\":-5,\"cate_one\":12,\"cate_two\":12,\"cate_three\":12,\"cate_four\":12,\"cate_five\":12},\"table\":\"base_goods\",\"ts\":1603961174636,\"type\":\"UPDATE\"}\n";
    private static final String insert = "{\"data\":[{\"id\":\"548153\",\"approval_number\":\"国妆特进字J201901531\",\"product_name\":\"歌薇颜色护理膏5MB（配合歌薇双氧水使用）\",\"spec\":null,\"product_unit\":\"Kao Germany GmbH\",\"product_addr\":\"Pfungstädter Strasse 92-100 64297 Darmstadt， Germany\",\"approve_date\":\"2019-01-03\",\"end_date\":\"4\",\"goods_type\":\"13\",\"domestic_foreign\":\"2\",\"relation_id\":\"212634\",\"cate_one\":null,\"cate_two\":null,\"cate_three\":null,\"cate_four\":null,\"cate_five\":null}],\"database\":\"cn_uniondrug_middleend_goodscenter\",\"es\":1603961228000,\"id\":9,\"isDdl\":false,\"mysqlType\":{\"id\":\"bigint(20)\",\"approval_number\":\"varchar(255)\",\"product_name\":\"varchar(512)\",\"spec\":\"text\",\"product_unit\":\"varchar(512)\",\"product_addr\":\"text\",\"approve_date\":\"varchar(255)\",\"end_date\":\"varchar(255)\",\"goods_type\":\"int(20)\",\"domestic_foreign\":\"int(20)\",\"relation_id\":\"bigint(20)\",\"cate_one\":\"varchar(255)\",\"cate_two\":\"varchar(255)\",\"cate_three\":\"varchar(255)\",\"cate_four\":\"varchar(255)\",\"cate_five\":\"varchar(255)\"},\"old\":null,\"pkNames\":[\"id\"],\"sql\":\"\",\"sqlType\":{\"id\":-5,\"approval_number\":12,\"product_name\":12,\"spec\":-4,\"product_unit\":12,\"product_addr\":2005,\"approve_date\":12,\"end_date\":12,\"goods_type\":4,\"domestic_foreign\":4,\"relation_id\":-5,\"cate_one\":12,\"cate_two\":12,\"cate_three\":12,\"cate_four\":12,\"cate_five\":12},\"table\":\"base_goods\",\"ts\":1603961228080,\"type\":\"INSERT\"}\n";
    private static final String delete = "{\"data\":[{\"id\":\"548153\",\"approval_number\":\"国妆特进字J201901531\",\"product_name\":\"歌薇颜色护理膏5MB（配合歌薇双氧水使用）\",\"spec\":null,\"product_unit\":\"Kao Germany GmbH\",\"product_addr\":\"Pfungstädter Strasse 92-100 64297 Darmstadt， Germany\",\"approve_date\":\"2019-01-03\",\"end_date\":\"4\",\"goods_type\":\"13\",\"domestic_foreign\":\"2\",\"relation_id\":\"212634\",\"cate_one\":null,\"cate_two\":null,\"cate_three\":null,\"cate_four\":null,\"cate_five\":null}],\"database\":\"cn_uniondrug_middleend_goodscenter\",\"es\":1603961318000,\"id\":10,\"isDdl\":false,\"mysqlType\":{\"id\":\"bigint(20)\",\"approval_number\":\"varchar(255)\",\"product_name\":\"varchar(512)\",\"spec\":\"text\",\"product_unit\":\"varchar(512)\",\"product_addr\":\"text\",\"approve_date\":\"varchar(255)\",\"end_date\":\"varchar(255)\",\"goods_type\":\"int(20)\",\"domestic_foreign\":\"int(20)\",\"relation_id\":\"bigint(20)\",\"cate_one\":\"varchar(255)\",\"cate_two\":\"varchar(255)\",\"cate_three\":\"varchar(255)\",\"cate_four\":\"varchar(255)\",\"cate_five\":\"varchar(255)\"},\"old\":null,\"pkNames\":[\"id\"],\"sql\":\"\",\"sqlType\":{\"id\":-5,\"approval_number\":12,\"product_name\":12,\"spec\":-4,\"product_unit\":12,\"product_addr\":2005,\"approve_date\":12,\"end_date\":12,\"goods_type\":4,\"domestic_foreign\":4,\"relation_id\":-5,\"cate_one\":12,\"cate_two\":12,\"cate_three\":12,\"cate_four\":12,\"cate_five\":12},\"table\":\"base_goods\",\"ts\":1603961318526,\"type\":\"DELETE\"}";

    public static void main(String[] args) {
        /*PolarDbBinlog binlog = JSON.parseObject(update, PolarDbBinlog.class);
        BaseGoods baseGoods = JSON.parseObject(JSON.toJSONString(binlog.getData().get(0)), BaseGoods.class);
        System.out.println(JSON.toJSONString(baseGoods));*/
        Integer [] arr = {1, 2, 3, 4, 5};
        List<Integer> l = Arrays.asList(arr);
        l.stream().forEach(i -> {
            if (i == 2) {
                return;
            }
            System.out.println(i);
        });
    }


}
