package cn.wr.map;

import cn.wr.model.CanalDataModel;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author RWang
 * @Date 2022/5/13
 */

public class CanalTransModelMap extends RichMapFunction<String, CanalDataModel> {
    private static final long serialVersionUID = -5722702092615839817L;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public CanalDataModel map(String value) throws Exception {
        return objectMapper.readValue(value, CanalDataModel.class);
    }
}
