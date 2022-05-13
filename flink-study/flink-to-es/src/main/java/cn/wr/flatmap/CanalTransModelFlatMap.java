package cn.wr.flatmap;

import cn.wr.model.CanalDataModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.util.Collector;

/**
 * 接收canal 主体数据
 * @author RWang
 * @Date 2022/5/11
 */

public class CanalTransModelFlatMap extends RichFlatMapFunction<String, CanalDataModel> {

    private static final long serialVersionUID = -6955035232169025277L;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void flatMap(String value, Collector<CanalDataModel> collector) throws Exception {
        if (StringUtils.isBlank(value)) return;
        CanalDataModel canalDataModel = objectMapper.readValue(value, CanalDataModel.class);
        collector.collect(canalDataModel);
    }
}
