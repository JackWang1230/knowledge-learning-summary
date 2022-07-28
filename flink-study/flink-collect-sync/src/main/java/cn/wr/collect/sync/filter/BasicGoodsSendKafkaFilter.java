package cn.wr.collect.sync.filter;

import cn.wr.collect.sync.constants.SchemaTableRelative;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.basic.BasicModel;
import org.apache.flink.api.common.functions.FilterFunction;


public class BasicGoodsSendKafkaFilter implements FilterFunction<BasicModel<Model>> {
    private static final long serialVersionUID = -1961258992652255443L;

    @Override
    public boolean filter(BasicModel<Model> model) throws Exception {
        return SchemaTableRelative.checkBasicGoods2KafkaValid(model.getTableName());
    }
}
