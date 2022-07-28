package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.constants.PropertiesConstants;
import cn.wr.collect.sync.flatmap.BasicBinlogFlatMapV2;
import cn.wr.collect.sync.keyby.BinlogKeyBy;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.PolarDbBinlogBatch;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.sink.*;
import cn.wr.collect.sync.utils.ExecutionEnvUtil;
import cn.wr.collect.sync.utils.KafkaConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;

public class BasicBinlogConsumerV2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicBinlogConsumerV2.class);

    public static void main(String[] args) {
        LOGGER.info("BasicBinlogConsumerV2 start ......");
        try {
            // 初始化参数
            String proFilePath = ParameterTool.fromArgs(args).get("conf");

            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);
            if (null == parameterTool) {
                LOGGER.info("BasicBinlogConsumerV2 parameterTool is null");
                return;
            }

            // 获取环境
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            // 接收kafka数据
            DataStreamSource<PolarDbBinlogBatch> dataSource = KafkaConfigUtil.buildBasicSource(env)
                    .setParallelism(parameterTool.getInt(STREAM_BASIC2CACHE_SOURCE_PARALLELISM, 5));

            // 拆分数据
            SingleOutputStreamOperator<BasicModel<Model>> splitData = dataSource
                    .flatMap(new BasicBinlogFlatMapV2())
                    .setParallelism(parameterTool.getInt(STREAM_BASIC2CACHE_FLATMAP_PARALLELISM, 5));

            // 写入 hbase/redis
            splitData.keyBy(new BinlogKeyBy())
                    .addSink(new BasicSyncSinkV2())
                    .setParallelism(parameterTool.getInt(PropertiesConstants.STREAM_BASIC2CACHE_SINK01_PARALLELISM))
                    .name("sink-basic");

            // 同步进度缓存 redis
            if (parameterTool.getBoolean(SINK_CACHE_PRODUCT_PROGRESS) || parameterTool.getBoolean(SINK_CACHE_STATE_PROGRESS)) {
                splitData.keyBy(new BinlogKeyBy())
                        .addSink(new SyncProgressSink())
                        .setParallelism(parameterTool.getInt(PropertiesConstants.STREAM_BASIC2CACHE_SINK02_PARALLELISM))
                        .name("sink-progress");
            }

            // 缓存 DTP 门店数据
            splitData
                    .keyBy(new BinlogKeyBy())
                    .filter((FilterFunction<BasicModel<Model>>) model -> Objects.nonNull(model)
                            && StringUtils.isNotBlank(model.getTableName())
                            && (StringUtils.equals(model.getTableName(), Table.BaseDataTable.organize_base.name())
                            || StringUtils.equals(model.getTableName(), Table.BaseDataTable.partners.name())))
                    .setParallelism(parameterTool.getInt(PropertiesConstants.STREAM_BASIC2CACHE_SINK02_PARALLELISM))
                    .name("filter-dtp-store")

                    .keyBy(new BinlogKeyBy())
                    .addSink(new DtpStoreSink())
                    .setParallelism(parameterTool.getInt(PropertiesConstants.STREAM_BASIC2CACHE_SINK02_PARALLELISM))
                    .name("sink-dtp-store");

            // 缓存 dbId 与 merchantId 关系
            splitData.keyBy(new BinlogKeyBy())
                    .filter((FilterFunction<BasicModel<Model>>) model -> Objects.nonNull(model)
                    && StringUtils.isNotBlank(model.getTableName())
                    && StringUtils.equals(model.getTableName(), Table.BaseDataTable.partners.name()))
                    .setParallelism(parameterTool.getInt(PropertiesConstants.STREAM_BASIC2CACHE_SINK02_PARALLELISM))
                    .name("filter-db-merchant")

                    .keyBy(new BinlogKeyBy())
                    .addSink(new DbMerchantSink())
                    .setParallelism(parameterTool.getInt(PropertiesConstants.STREAM_BASIC2CACHE_SINK02_PARALLELISM))
                    .name("sink-db-merchant");

            // 缓存 organize_base
            splitData.keyBy(new BinlogKeyBy())
                    .filter((FilterFunction<BasicModel<Model>>) model -> Objects.nonNull(model)
                    && StringUtils.isNotBlank(model.getTableName())
                    && StringUtils.equals(model.getTableName(), Table.BaseDataTable.organize_base.name()))
                    .setParallelism(parameterTool.getInt(PropertiesConstants.STREAM_BASIC2CACHE_SINK02_PARALLELISM))
                    .name("filter-organize-base")

                    .keyBy(new BinlogKeyBy())
                    .addSink(new OrganizeBaseSink())
                    .setParallelism(parameterTool.getInt(PropertiesConstants.STREAM_BASIC2CACHE_SINK02_PARALLELISM))
                    .name("sink-organize-base");

            // 基础商品表变更触发缓存字段
            /*if (parameterTool.getBoolean(SINK_CACHECHANGE)) {
                // 写入待定时刷新缓存字段
                splitData.addSink(new CacheChangeSink())
                        .setParallelism(parameterTool.getInt(PropertiesConstants.STREAM_BASIC2CACHE_SINK02_PARALLELISM))
                        .name("sink-cache");
            }*/

            env.execute("[PRD][HBASE/REDIS] - basic_goods_2_cache(" + parameterTool.get(FLINK_COLLECT_VERSION) + ")");
        }
        catch (Exception e) {
            LOGGER.error("BasicBinlogConsumerV2 msg: {} error: {}", e.getMessage(), e);
        }
    }
}
