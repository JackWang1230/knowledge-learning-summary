package cn.wr.flatmap;

import cn.wr.model.BasicModel;
import cn.wr.model.CanalDataModel;
import cn.wr.model.price.PriceListDetails;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static cn.wr.constants.PropertiesConstants.*;

/**
 * 基于price_list_details的操作类型进行组装数据
 * @author RWang
 * @Date 2022/8/16
 */

public class PriceListDetailsFlatMap extends RichFlatMapFunction<CanalDataModel, BasicModel<PriceListDetails>> {
    private static final long serialVersionUID = -349892826580415796L;

    private static final Logger logger = LoggerFactory.getLogger(PriceListDetailsFlatMap.class);

    @Override
    public void flatMap(CanalDataModel value, Collector<BasicModel<PriceListDetails>> out) throws Exception {

        if (Objects.isNull(value)) return;
        int length = value.getData().size();
        String operate = value.getType().toUpperCase();
        for (int i = 0; i < length; i++) {
            switch (operate) {
                case INSERT:
                    out.collect(reflectData(value.getTable(), operate, value.getData().get(i), null));
                    break;
                case DELETE:
                    logger.info("PriceListDetailsFlatMap delete: {}", JSON.toJSONString(value));
                    out.collect(reflectData(value.getTable(), operate, value.getData().get(i), null));
                    break;
                case UPDATE:
                    // 若rowkey 发生变化 需要先将旧数据删除，再更新新数据
                    if (isRowKey(value.getOld().get(i))) {
                        out.collect(reflectOld(value.getTable(), UPDATE_DELETE, value.getOld().get(i)));
                    }
                    out.collect(reflectData(value.getTable(), operate, value.getData().get(i), null));
                    break;
                default:
                    logger.error("PriceListDetailsFlatMap flatMap unknown operate: {}", JSON.toJSONString(value));
                    break;
            }
        }

    }

    /**
     *  转换新数据
     * @param tableName 表名
     * @param operate 操作类型
     * @param dataMap 新数据
     * @param oldMap 老数据
     * @return BasicModel<PriceListDetails>
     */
    public BasicModel<PriceListDetails> reflectData(String tableName,
                                                    String operate,
                                                    Object dataMap,
                                                    Object oldMap
    ) {
        BasicModel<PriceListDetails> basicMode = new BasicModel<>();
        ObjectMapper objectMapper = new ObjectMapper();

        basicMode.setData(objectMapper.convertValue(dataMap, PriceListDetails.class));

        if (Objects.nonNull(oldMap)) {
            basicMode.setOld(objectMapper.convertValue(oldMap, PriceListDetails.class));
        }
        basicMode.setTableName(tableName);
        basicMode.setOperate(operate);
        return basicMode;
    }

    /**
     *  转换老数据
     * @param tableName 表名
     * @param operate 操作类型
     * @param oldMap 老数据
     * @return BasicModel<PriceListDetails>
     */
    public static BasicModel<PriceListDetails> reflectOld(String tableName,
                                                          String operate,
                                                          Object oldMap
    ) {

        ObjectMapper objectMapper = new ObjectMapper();
        PriceListDetails priceListDetails = null;
        if (Objects.nonNull(oldMap)) {
            priceListDetails = objectMapper.convertValue(oldMap, PriceListDetails.class);
        }
        return new BasicModel<>(tableName, operate, priceListDetails, null);
    }

    /**
     * 判断变更数据是否是rowKey
     * @param oldMap oldMap
     * @return boolean
     */
    public boolean isRowKey(Object oldMap) {
        ObjectMapper objectMapper = new ObjectMapper();
        PriceListDetails priceListDetails = null;
        if (Objects.nonNull(oldMap)) {
            priceListDetails = objectMapper.convertValue(oldMap, PriceListDetails.class);
        }
        return StringUtils.isNotBlank(priceListDetails.getDetailKey());

    }
}
