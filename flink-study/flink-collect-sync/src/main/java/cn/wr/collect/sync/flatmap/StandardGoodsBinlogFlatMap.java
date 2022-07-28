package cn.wr.collect.sync.flatmap;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.function.FieldCheck;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.PolarDbBinlogBatch;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.utils.ReflectUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static cn.wr.collect.sync.constants.CommonConstants.OPERATE_UPDATE_DELETE;

public class StandardGoodsBinlogFlatMap extends RichFlatMapFunction<PolarDbBinlogBatch, BasicModel<Model>> {
    private static final long serialVersionUID = 1199258764938552182L;
    private static final Logger log = LoggerFactory.getLogger(StandardGoodsBinlogFlatMap.class);

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void flatMap(PolarDbBinlogBatch binlog, Collector<BasicModel<Model>> collector) throws Exception {
        if (Objects.isNull(binlog)) {
            return;
        }
        Class<? extends Model> clazz = Table.BaseDataTable.getClazz(binlog.getTable());
        if (Objects.isNull(clazz)) {
            return;
        }
        int length = binlog.getData().size();
        String operate = StringUtils.lowerCase(binlog.getType());

        for (int i = 0; i < length; i++) {
            switch (operate) {
                case CommonConstants.OPERATE_INSERT:
                    collector.collect(ReflectUtil.reflectData(binlog.getTable(), operate, binlog.getData().get(i),
                            null, clazz));
                    break;

                case CommonConstants.OPERATE_DELETE:
                    log.info("StandardGoodsBinlogFlatMap delete: table:{} operate:{}, data:{}",
                            binlog.getTable(), operate, JSON.toJSONString(binlog.getData()));
                    collector.collect(ReflectUtil.reflectData(binlog.getTable(), operate, binlog.getData().get(i),
                            null, clazz));
                    break;

                case CommonConstants.OPERATE_UPDATE:
                    // 判断es关联字段是否发生变更，如果更新是无关字段，则跳过
                    if (!FieldCheck.standardFieldCheck(binlog.getOld().get(i).keySet(), binlog.getTable())) {
                        log.info("StandardGoodsBinlogFlatMap not need update");
                        continue;
                    }

                    // 判断是否有必要处理旧数据，key发生变更的情况下才需要处理旧数据
                    if (FieldCheck.standardKeyCheck(binlog.getOld().get(i).keySet(), binlog.getTable())) {
                        log.info("StandardGoodsBinlogFlatMap update_delete: table:{} operate:{}, data:{}, old:{}",
                                binlog.getTable(), operate, JSON.toJSONString(binlog.getData()),
                                JSON.toJSONString(binlog.getOld()));

                        // 删除旧数据
                        collector.collect(ReflectUtil.reflectOld(binlog.getTable(), OPERATE_UPDATE_DELETE,
                                binlog.getData().get(i),
                                binlog.getOld().get(i), clazz));

                    }

                    // 更新新数据
                    collector.collect(ReflectUtil.reflectData(binlog.getTable(), operate,
                            binlog.getData().get(i),
                            binlog.getOld().get(i), clazz));
                    break;

                default:
                    log.error("BinlogList2SingleFlatMapV2 flatMap unknown operate: {}", JSON.toJSONString(binlog));
                    break;
            }
        }
    }
}
