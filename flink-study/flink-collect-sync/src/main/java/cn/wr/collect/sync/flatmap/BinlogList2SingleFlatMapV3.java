package cn.wr.collect.sync.flatmap;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.PolarDbBinlogBatch;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.utils.ReflectUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


public class BinlogList2SingleFlatMapV3 extends RichFlatMapFunction<PolarDbBinlogBatch, BasicModel<Model>> {
    private static final long serialVersionUID = -4562858843760184440L;
    private static final Logger log = LoggerFactory.getLogger(BinlogList2SingleFlatMapV3.class);

    @Override
    public void flatMap(PolarDbBinlogBatch batch, Collector<BasicModel<Model>> collector) {
        if (Objects.isNull(batch)) {
            return;
        }
        Class<? extends Model> clazz = Table.BaseDataTable.getClazz(batch.getTable());
        if (Objects.isNull(clazz)) {
            return;
        }
        int length = batch.getData().size();
        String operate = StringUtils.lowerCase(batch.getType());

        for (int i = 0; i < length; i++) {
            switch (operate) {
                case CommonConstants.OPERATE_INSERT:
                case CommonConstants.OPERATE_DELETE:
                    collector.collect(ReflectUtil.reflectData(batch.getTable(), operate, batch.getData().get(i),
                            null, clazz));
                    break;

                case CommonConstants.OPERATE_UPDATE:
                    // 更新新数据
                    collector.collect(ReflectUtil.reflectData(batch.getTable(), operate,
                            batch.getData().get(i),
                            batch.getOld().get(i), clazz));
                    break;

                default:
                    log.error("BinlogList2SingleFlatMapV3 flatMap unknown operate: {}", JSON.toJSONString(batch));
                    break;
            }
        }
    }
}
