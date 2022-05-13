package cn.wr.flatmap;


import cn.wr.model.GcConfigSkuStar;
import cn.wr.model.GoodsSkuStar;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.Objects;


/**
 * @author RWang
 * @Date 2022/5/12
 */

@Deprecated
public class GoodsSkuStarFlatMap extends RichFlatMapFunction<GcConfigSkuStar, GoodsSkuStar> {

    private static final long serialVersionUID = -1440273794077441191L;

    @Override
    public void flatMap(GcConfigSkuStar value, Collector<GoodsSkuStar> collector) throws Exception {

        if (Objects.isNull(value)) {
            return;
        }
        GoodsSkuStar goodsSkuStar = new GoodsSkuStar();
        goodsSkuStar.setSkuNo(value.getSkuNo());
        goodsSkuStar.setMerchantId(value.getMerchantId());
        goodsSkuStar.setIsApprovalNumber(value.getIsApprovalNumber());
        goodsSkuStar.setIsTradecode(value.getIsTradecode());
        goodsSkuStar.setIsManufacturer(value.getIsManufacturer());
        goodsSkuStar.setIsSpecName(value.getIsSpecName());
        goodsSkuStar.setIsGoodsName(value.getIsGoodsName());
        collector.collect(goodsSkuStar);
    }
}
