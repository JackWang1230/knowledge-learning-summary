package cn.wr.collect.sync.scheme;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.model.MetricEvent;
import cn.wr.collect.sync.model.MetricItem;
import cn.wr.collect.sync.model.gc.PlatformGoods;
import cn.wr.collect.sync.utils.Binlog2ModelConverter;
import cn.wr.collect.sync.utils.TiDbDrainerBinlog;
import com.google.gson.Gson;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static cn.wr.collect.sync.constants.CommonConstants.PLATFORM_GOODS;
import static cn.wr.collect.sync.constants.CommonConstants.SCHEMA_GOODS_CENTER_TIDB;

@Deprecated
public class PlatformGoodsBinlogSchema implements DeserializationSchema<MetricEvent>, SerializationSchema<MetricEvent> {
    private static final long serialVersionUID = 5958003487280393782L;
    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformGoodsBinlogSchema.class);
    private static final Gson gson = new Gson();

    @Override
    public MetricEvent deserialize(byte[] bytes) throws IOException {
        // 解析binlog
        return handle(bytes);
    }

    @Override
    public boolean isEndOfStream(MetricEvent metricEvent) {
        return false;
    }

    @Override
    public byte[] serialize(MetricEvent metricEvent) {
        return gson.toJson(metricEvent).getBytes(Charset.forName("UTF-8"));
    }

    @Override
    public TypeInformation<MetricEvent> getProducedType() {
        return TypeInformation.of(MetricEvent.class);
    }

    private MetricEvent handle(byte[] message) {
//        long start = System.currentTimeMillis();
        MetricEvent metricEvent = new MetricEvent();
        TiDbDrainerBinlog.Binlog binlog;
        try {
            binlog = TiDbDrainerBinlog.Binlog.parseFrom(message);
        } catch (InvalidProtocolBufferException e) {
            LOGGER.error("### PlatformGoodsBinlogSchema InvalidProtocolBufferException error: {}", e);
            return metricEvent;
        }
        if (!binlog.hasDmlData()) {
            // LOGGER.info("### PlatformGoodsBinlogSchema binlog return, is not dml data");
            return metricEvent;
        }
        List<MetricItem> itemList = new ArrayList<>();
        for (TiDbDrainerBinlog.Table table : binlog.getDmlData().getTablesList()) {

            if (StringUtils.equals(SCHEMA_GOODS_CENTER_TIDB, table.getSchemaName()) && StringUtils.equals(PLATFORM_GOODS, table.getTableName())) {
                // 源数据表处理
                itemList.addAll(transformTableColumns(table));
            }
        }
        metricEvent.setFields(itemList);
//        long end = System.currentTimeMillis();
//        LOGGER.info("### GoodsBinlogDeserializationSchema size:{}, time:{}(ms)", itemList.size(), (end - start));
        return metricEvent;
    }

    /**
     * 处理表 platform_goods
     *
     * @param table
     * @return
     */
    private List<MetricItem> transformTableColumns(TiDbDrainerBinlog.Table table) {
        List<MetricItem> itemList = new ArrayList<>();
        for (TiDbDrainerBinlog.TableMutation mutation : table.getMutationsList()) {
            try {
                String operate = getOperate(mutation);
                if (StringUtils.equals(CommonConstants.OPERATE_UNKNOWN, operate)) {
                    continue;
                }
                if (StringUtils.equals(CommonConstants.PLATFORM_GOODS, table.getTableName())) {
                    PlatformGoods newGoods = Binlog2ModelConverter.convertToPlatformGoods(table.getColumnInfoList(), mutation.getRow());
                    // LOGGER.info("### transformTableColumns PlatformGoods operate:{} json:{}, ", operate, newGoods);
                    // 查询拆分数据
                    itemList.add(new MetricItem(table.getTableName(), operate, newGoods));
                }
            } catch (Exception e) {
                LOGGER.error("### PlatformGoodsBinlogSchema Exception error {}", e);
            }
        }

        return itemList;
    }

    /**
     * 获取数据库操作
     *
     * @param mutation
     * @return
     */
    private String getOperate(TiDbDrainerBinlog.TableMutation mutation) {
        switch (mutation.getType()) {
            case Delete:
                return CommonConstants.OPERATE_DELETE;
            case Insert:
                return CommonConstants.OPERATE_INSERT;
            case Update:
                return CommonConstants.OPERATE_UPDATE;
            default:
                return CommonConstants.OPERATE_UNKNOWN;
        }
    }
}
