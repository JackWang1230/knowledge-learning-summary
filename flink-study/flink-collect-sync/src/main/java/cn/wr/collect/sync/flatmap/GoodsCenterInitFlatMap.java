package cn.wr.collect.sync.flatmap;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.dao.partner.PartnerGoodsDao;
import cn.wr.collect.sync.model.goodscenter.GoodsCenterDTO;
import cn.wr.collect.sync.model.middledb.PgcMerchantInfoShortInit;
import cn.wr.collect.sync.model.partner.PartnerGoods;
import cn.wr.collect.sync.utils.QueryUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class GoodsCenterInitFlatMap extends RichFlatMapFunction<PgcMerchantInfoShortInit, GoodsCenterDTO> {
    private static final long serialVersionUID = -4873480199609663736L;
    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsCenterInitFlatMap.class);
    private ParameterTool parameterTool;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void flatMap(PgcMerchantInfoShortInit shortInit, Collector<GoodsCenterDTO> collector) {
        if (null == shortInit) {
            return;
        }

        collectData(new PartnerGoodsDao(parameterTool), shortInit, collector);
    }

    private void collectData(QueryLimitDao<PartnerGoods> queryDao, PgcMerchantInfoShortInit shortInit, Collector<GoodsCenterDTO> collector) {
        // 获取表对应的实体类
        int i = 0;
        Long id = 0L;
        List<PartnerGoods> models;
        Map<String, Object> params = new HashMap<>();
        if (Objects.nonNull(shortInit.getStartTime()) && Objects.nonNull(shortInit.getEndTime())) {
            do {
                params.put("id", id);
                params.put("complementStartTime", shortInit.getStartTime());
                params.put("complementEndTime", shortInit.getEndTime());
                params.put("dbId", shortInit.getDbId());
                // 从数据库查询数据,一次查1w条
                models = QueryUtil.findLimitPlus(queryDao, i, params, null);

                id = this.assembleGoodsCenterDTO(shortInit, collector, id, models);
                i++;
            } while (models.size() == QueryUtil.QUERY_PAGE_SIZE * QueryUtil.its.length);
        } else if (Objects.nonNull(shortInit.getDbId())) {
            do {
                params.put("id", id);
                params.put("dbId", shortInit.getDbId());
                if (StringUtils.isNotBlank(shortInit.getGoodsInternalId())) {
                    params.put("goodsInternalId", shortInit.getGoodsInternalId());
                }
                // 从数据库查询数据,一次查1w条
                models = QueryUtil.findLimitPlus(queryDao, i, params, null);
                // LOGGER.info("### BasicTimeSyncSource collectData page:{} size:{}", i, models.size());
                id = this.assembleGoodsCenterDTO(shortInit, collector, id, models);
                i++;
            } while (models.size() == QueryUtil.QUERY_PAGE_SIZE * QueryUtil.its.length);
        }
    }

    private Long assembleGoodsCenterDTO(PgcMerchantInfoShortInit shortInit, Collector<GoodsCenterDTO> collector, Long id, List<PartnerGoods> models) {
        if (CollectionUtils.isEmpty(models)) {
            return id;
        }
        models.forEach(partnerGoods ->
                shortInit.getMerchantInfos()
                        .stream()
                        .filter(Objects::nonNull)
                        .forEach(infoShort -> {
                            GoodsCenterDTO dto = new GoodsCenterDTO().convert(partnerGoods, infoShort, CommonConstants.OPERATE_SHORT_INSERT);
                            LOGGER.info("GoodsCenterInitFlatMap json: {}", JSON.toJSONString(dto));
                            collector.collect(dto);
                        }));
        id = models.get(models.size() - 1).getId();
        return id;
    }
}
