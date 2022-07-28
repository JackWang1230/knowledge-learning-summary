package cn.wr.collect.sync.filter;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.kafka.ElasticGoodsDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class GoodsChangeStoreFilter implements FilterFunction<BasicModel<ElasticGoodsDTO>> {
    private static final long serialVersionUID = -1961258992652255443L;
    private static final Logger log = LoggerFactory.getLogger(GoodsChangeStoreFilter.class);

    @Override
    public boolean filter(BasicModel<ElasticGoodsDTO> model) throws Exception {
        if (Objects.isNull(model) || StringUtils.isBlank(model.getOperate())
                || Objects.isNull(model.getData())) {
//            log.info("GoodsChangeStoreFilter model is not valid:{}", JSON.toJSONString(model));
            return false;
        }

        ElasticGoodsDTO data = model.getData();
        if (StringUtils.isBlank(data.getTradeCode())) {
//            log.info("GoodsChangeStoreFilter tradeCode is not valid:{}", JSON.toJSONString(model));
            return false;
        }
        if ((StringUtils.equals(CommonConstants.OPERATE_INSERT, model.getOperate())
                || StringUtils.equals(CommonConstants.OPERATE_UPDATE, model.getOperate()))
                && (Objects.isNull(data.getMerchantId()) || Objects.isNull(data.getStoreId()))) {
//            log.info("GoodsChangeStoreFilter data is not valid:{}", JSON.toJSONString(model));
            return false;
        }
        return true;
    }
}
