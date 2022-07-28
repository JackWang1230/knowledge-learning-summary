package cn.wr.collect.sync.keyby;

import cn.wr.collect.sync.constants.SymbolConstants;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.QueryField;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.partner.PartnerGoods;
import cn.wr.collect.sync.model.partner.PartnerStoreGoods;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.functions.KeySelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.wr.collect.sync.constants.CommonConstants.PARTNER_GOODS;
import static cn.wr.collect.sync.constants.CommonConstants.PARTNER_STORE_GOODS;


public class BinlogKeyByGoods implements KeySelector<BasicModel<Model>, String> {
    private static final long serialVersionUID = -7292926577568477253L;
    private static final Logger log = LoggerFactory.getLogger(BinlogKeyByGoods.class);

    @Override
    public String getKey(BasicModel<Model> model) throws Exception {
        if (StringUtils.isBlank(model.getOperate()) || StringUtils.isBlank(model.getTableName())
            || Objects.isNull(model.getData())) {
            return null;
        }
        switch (model.getTableName()) {
            case PARTNER_GOODS:
                return ((PartnerGoods) model.getData()).getDbId() + SymbolConstants.HOR_LINE
                        + ((PartnerGoods) model.getData()).getInternalId();

            case PARTNER_STORE_GOODS:
                return ((PartnerStoreGoods) model.getData()).getDbId() + SymbolConstants.HOR_LINE
                        + ((PartnerStoreGoods) model.getData()).getGoodsInternalId();

            default:
                log.info("BinlogKeyByGoods ERROR DATA :{}", JSON.toJSONString(model));
                List<Object> keyList = Arrays.stream(model.getData().getClass().getDeclaredFields())
                        .filter(f -> f.isAnnotationPresent(QueryField.class))
                        .sorted(Comparator.comparingInt(f -> f.getDeclaredAnnotation(QueryField.class).order()))
                        .map(f -> {
                            try {
                                f.setAccessible(true);
                                return f.get(model.getData());
                            } catch (IllegalAccessException e) {
                                log.error("BinlogKeyBy IllegalAccessException: {}", e);
                            }
                            return null;
                        })
                        .collect(Collectors.toList());
                return model.getTableName() + SymbolConstants.HOR_LINE + StringUtils.join(keyList, SymbolConstants.HOR_LINE);
        }
    }
}
