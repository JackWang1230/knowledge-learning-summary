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
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class BasicBinlogFlatMapV2 implements FlatMapFunction<PolarDbBinlogBatch, BasicModel<Model>> {
    private static final long serialVersionUID = 2776424086020639747L;
    private static final Logger log = LoggerFactory.getLogger(BasicBinlogFlatMapV2.class);

    @Override
    public void flatMap(PolarDbBinlogBatch binlog, Collector<BasicModel<Model>> collector) {
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
                    log.info("BasicBinlogFlatMapV2 delete: {}", JSON.toJSONString(binlog));
                    collector.collect(ReflectUtil.reflectData(binlog.getTable(), operate, binlog.getData().get(i),
                            null, clazz));
                    break;

                case CommonConstants.OPERATE_UPDATE:
                    // 判断是否有必要删除旧数据，key发生变更的情况下才需要删除旧数据
                    if (FieldCheck.rhKeyCheck(binlog.getOld().get(i), clazz)) {
                        log.info("BasicBinlogFlatMapV2 update_delete:{}", JSON.toJSONString(binlog));

                        // 删除旧数据
                        collector.collect(ReflectUtil.reflectOld(binlog.getTable(), CommonConstants.OPERATE_UPDATE_DELETE,
                                binlog.getData().get(i),
                                binlog.getOld().get(i), clazz));
                    }

                    // 更新新数据
                    collector.collect(ReflectUtil.reflectData(binlog.getTable(), operate,
                            binlog.getData().get(i),
                            binlog.getOld().get(i), clazz));

                    break;

                default:
                    log.error("BasicBinlogFlatMapV2 flatMap unknown operate: {}", JSON.toJSONString(binlog));
                    break;
            }
        }
    }
}
