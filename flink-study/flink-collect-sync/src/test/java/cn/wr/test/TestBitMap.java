package cn.wr.test;

//import akka.io.dns.AAAARecord;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.constants.SymbolConstants;
import cn.wr.collect.sync.model.ElasticO2O;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.QueryField;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.gc.PartnerGoodsImg;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Program: partner-goods-collect
 * @ClassName: TestBitMap
 * @Description: bitmap
 * @Author: zhu.liqing
 * @Date: 2021-01-11 14:29
 * @Version: 1.0.0
 */
public class TestBitMap {
    class Test implements Runnable {
        private ElasticO2O o2o;
        public Test(ElasticO2O o2o) {
            this.o2o = o2o;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(o2o.getSkuCode());
        }
    }

    public static void main(String[] args) throws Exception {
        /*List<PgConcatParams> collect = Arrays.stream(new String[]{"1", "2"}).map(PgConcatParams::new).collect(Collectors.toList());
        System.out.println(JSON.toJSONString(collect));*/
        /*ElasticO2O o2o = new ElasticO2O();
        int i = 0;
        ElasticO2O.SinkField sinkField = o2o.new SinkField();
        sinkField.setFieldField(Arrays.asList(i + ""));
        o2o.setSinkField(sinkField);
        while (i < 10) {
            ElasticO2O o = (ElasticO2O) o2o.clone();


            o.setSkuCode(Thread.currentThread().getName() + "-" + i);
            Test test = new TestBitMap().new Test(o);
            Thread thread = new Thread(test);
            thread.start();
            System.out.println("t" + i);
            i ++;
        }*/
        BasicModel<Model> model = new BasicModel<>();
        model.setTableName(Table.BaseDataTable.partner_goods_img.name());
        model.setOperate(CommonConstants.OPERATE_INSERT);
        PartnerGoodsImg goodsImg = new PartnerGoodsImg();
        model.setData(goodsImg);
        List<Object> keyList = Arrays.stream(model.getData().getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(QueryField.class))
                .sorted(Comparator.comparingInt(f -> f.getDeclaredAnnotation(QueryField.class).order()))
                .map(f -> {
                    try {
                        f.setAccessible(true);
                        return f.get(model.getData());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());
        String key = StringUtils.join(keyList, SymbolConstants.HOR_LINE);
        System.out.println(key);
    }
}
