package cn.wr.collect.sync.constants;

import cn.wr.collect.sync.model.goodscenter.GoodsCenterDTO;
import cn.wr.collect.sync.model.stock.StockGoods;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.OutputTag;

public class OutputTagConst {
    // dtp 旁路输出
    public static final OutputTag<GoodsCenterDTO> OUT_PUT_TAG_DTP = new OutputTag<>("side-output-dtp",
            TypeInformation.of(GoodsCenterDTO.class));

    // 非 dtp 旁路输出
    public static final OutputTag<GoodsCenterDTO> OUT_PUT_TAG_NOT_DTP = new OutputTag<>("side-output-not-dtp",
            TypeInformation.of(GoodsCenterDTO.class));


    // dtp 旁路输出
    public static final OutputTag<StockGoods> OUT_PUT_TAG_STOCK_GOODS_DTP = new OutputTag<>("side-output-dtp",
            TypeInformation.of(StockGoods.class));

    // 非 dtp 旁路输出
    public static final OutputTag<StockGoods> OUT_PUT_TAG_STOCK_GOODS_NOT_DTP = new OutputTag<>("side-output-not-dtp",
            TypeInformation.of(StockGoods.class));

}
