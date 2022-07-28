package cn.wr.collect.sync.flatmap;

import cn.wr.collect.sync.constants.SymbolConstants;
import cn.wr.collect.sync.function.Compute;
import cn.wr.collect.sync.model.gc.*;
import cn.wr.collect.sync.model.standard.*;
import cn.wr.collect.sync.redis.RedisService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class StandardFlatMap extends RichFlatMapFunction<String, StandardElastic> {
    private static final long serialVersionUID = -1836441116630224459L;
    private static final Logger log = LoggerFactory.getLogger(StandardFlatMap.class);
    private RedisService redisService;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        final ParameterTool tool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        redisService = new RedisService(tool);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void flatMap(String data, Collector<StandardElastic> collector) throws Exception {
        log.info("StandardFlatMap kafka:{}", data);
        if (StringUtils.isBlank(data)) {
            log.info("StandardFlatMap data is blank");
            return;
        }

        StandKafkaDTO dto = JSON.parseObject(data, StandKafkaDTO.class);
        if (Objects.isNull(dto)) {
            log.info("StandardFlatMap parse json is null");
            return;
        }

        StandardElastic elastic = this.convert2Es(dto);
        if (Objects.isNull(elastic)) {
            log.info("StandardFlatMap conver result is null");
            return;
        }
        collector.collect(elastic);
    }

    /**
     * 参数转换
     * @param dto
     * @return
     */
    private StandardElastic convert2Es(StandKafkaDTO dto) {
        StandardElastic elastic = new StandardElastic();

        // 说明书
        this.convertManual(elastic, dto.getRedisManual());

        // 详情
        this.convertStandard(elastic, dto.getRedisStandard());

        // 是否超重
        this.convertOverweight(elastic, dto.getRedisStandard());

        // 是否处方
        this.convertIsPrescription(elastic, dto.getRedisStandard());

        // 是否麻黄碱
        this.convertIsEphedrine(elastic, dto.getRedisStandard());

        // 同步时间
        elastic.setSyncDate(LocalDateTime.now());

        return elastic;
    }

    /**
     * manual
     * @param elastic
     * @param manual
     */
    private void convertManual(StandardElastic elastic, RedisManual manual) {
        if (Objects.isNull(manual)) {
            return;
        }
        elastic.setSpecificationManual(manual.getSpecification());
        elastic.setCommonName(manual.getCommonName());
        elastic.setEnName(manual.getEnName());
        elastic.setPinyinName(manual.getPinyinName());
        elastic.setApprovalNumber(manual.getApprovalNumber());
        elastic.setTaboo(manual.getTaboo());
        elastic.setInteraction(manual.getInteraction());
        elastic.setComposition(manual.getComposition());
        elastic.setPharmacologicalEffects(manual.getPharmacologicalEffects());
        elastic.setDosage(manual.getDosage());
        elastic.setClinicalClassification(manual.getClinicalClassification());
        elastic.setCureDisease(manual.getCureDisease());
        elastic.setAttentions(manual.getAttentions());
        elastic.setManufacturer(manual.getManufacturer());
        elastic.setPharmacokinetics(manual.getPharmacokinetics());
        elastic.setStorage(manual.getStorage());
        elastic.setPediatricUse(manual.getPediatricUse());
        elastic.setGeriatricUse(manual.getGeriatricUse());
        elastic.setPregnancyAndNursingMothers(manual.getPregnancyAndNursingMothers());
        elastic.setOverDosage(manual.getOverDosage());
        elastic.setValidity(manual.getValidity());
        elastic.setDrugName(manual.getDrugName());
        elastic.setRelativeSickness(manual.getRelativeSickness());
        elastic.setPrescriptionType(manual.getPrescriptionType());
        elastic.setIndications(manual.getIndications());
        elastic.setDrugType(manual.getDrugType());
        elastic.setPackaging(manual.getPackaging());
        elastic.setSideEffect(manual.getSideEffect());
        elastic.setHint(manual.getHint());

        // mutexs
        if (Objects.nonNull(manual.getMutex()) && CollectionUtils.isNotEmpty(manual.getMutex().getMutexRefs())) {
            elastic.setMutexs(this.convertMutex(manual.getMutex().getMutexRefs()));
        }
    }

    /**
     * mutex
     * @param mutexRef
     * @return
     */
    private List<ManualMutex> convertMutex(List<RedisMutex.RedisMutexRef> mutexRef) {
        return mutexRef.stream()
                .map(item -> new ManualMutex().convert(item))
                .collect(Collectors.toList());
    }

    /**
     * standard
     * @param elastic
     * @param standard
     */
    private void convertStandard(StandardElastic elastic, RedisStandard standard) {
        if (Objects.isNull(standard)) {
            return;
        }
        elastic.setId(Objects.nonNull(standard.getId()) ? Long.valueOf(standard.getId()) : null);
        elastic.setSpuId(standard.getSpuId());
        elastic.setGoodsName(standard.getGoodsName());
        elastic.setMainTitle(standard.getMainTitle());
        elastic.setSubTitle(standard.getSubTitle());
        elastic.setTradeCode(standard.getTradeCode());
        elastic.setSpecificationStandard(standard.getSpecification());
        elastic.setRetailPrice(standard.getRetailPrice());
        elastic.setGrossWeight(standard.getGrossWeight());
        elastic.setOriginPlace(standard.getOriginPlace());
        elastic.setBrand(standard.getBrand());
        elastic.setCategoryId(standard.getCategoryId());
        elastic.setSearchKeywords(standard.getSearchKeywords());
        elastic.setSearchAssociationWords(standard.getSearchAssociationWords());
        elastic.setHighlights(standard.getHighlights());
        elastic.setDetailsCode(standard.getDetailsCode());
        elastic.setUrls(standard.getUrls());
        elastic.setStatus(standard.getStatus());
        elastic.setJoinRemarks(standard.getJoinRemarks());
        elastic.setWeight(standard.getWeight());
        elastic.setVolume(standard.getVolume());
        elastic.setMiddlePrice(standard.getMiddlePrice());
        elastic.setHistorySales(Objects.nonNull(standard.getHistorySales()) ? standard.getHistorySales() : new BigDecimal("0"));
        // 是否线上  0:线下1:线上
        elastic.setIsOnline(Objects.nonNull(standard.getStoreNum()) && standard.getStoreNum() > 0 ? 1 : 0);

        // attrs
        elastic.setAttrs(this.convertAttrs(standard));
        // cates
        elastic.setCates(this.convertCates(standard));

    }

    /**
     * attrs 参数转换
     * @param standard
     * @return
     */
    private List<String> convertAttrs(RedisStandard standard) {
        Set<String> attrs = new HashSet<>();
        if (CollectionUtils.isNotEmpty(standard.getRedisAttrs())) {
            standard.getRedisAttrs().forEach(item -> {
               if (StringUtils.isNotBlank(item.getId())) {
                    attrs.add(item.getId());
               }
               if (StringUtils.isNotBlank(item.getPids())) {
                   Arrays.asList(item.getPids().split(SymbolConstants.COMMA_EN))
                           .forEach(pid -> attrs.add(pid));
               }
            });
        }
        if (CollectionUtils.isEmpty(attrs)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(attrs);
    }

    /**
     * cates 参数转换
     * @param standard
     * @return
     */
    private List<String> convertCates(RedisStandard standard) {
        Set<String> cates = new HashSet<>();
        if (CollectionUtils.isNotEmpty(standard.getRedisCates())) {
            standard.getRedisCates().forEach(item -> {
                if (StringUtils.isNotBlank(item.getId())) {
                    cates.add(item.getId());
                }
                if (StringUtils.isNotBlank(item.getPids())) {
                    Arrays.asList(item.getPids().split(SymbolConstants.COMMA_EN))
                            .forEach(pid -> cates.add(pid));
                }
            });
        }
        if (CollectionUtils.isEmpty(cates)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(cates);
    }

    /**
     * 是否超重字段赋值
     * @param elastic
     * @param standard
     */
    public void convertOverweight(StandardElastic elastic, RedisStandard standard) {
        GoodsOverweight goodsOverweight = redisService.queryGoodsOverweight(standard.getTradeCode());
        elastic.setIsOverweight(Compute.isOverweight(goodsOverweight));
    }

    /**
     * 是否处方字段赋值
     * @param elastic
     * @param standard
     */
    public void convertIsPrescription(StandardElastic elastic, RedisStandard standard) {
        BaseNootc baseNootc = redisService.queryGcBaseNootc(standard.getApprovalNumber());
        GoodsDosage dosage = redisService.queryGcGoodsDosage(standard.getTradeCode());
        elastic.setIsPrescription(Compute.isPrescription(baseNootc, dosage));
    }

    /**
     * 是否麻黄碱字段赋值
     * @param elastic
     * @param standard
     */
    public void convertIsEphedrine(StandardElastic elastic, RedisStandard standard) {
        elastic.setIsEphedrine(false);
        List<GoodsSpuAttrSyncrds> spuAttrList = redisService.queryGcGoodsSpuAttrSyncrds(standard.getTradeCode());
        spuAttrList.forEach(spuAttr -> {
            GoodsAttrInfoSyncrds attr = redisService.queryGcGoodsAttrInfoSyncrds(spuAttr.getAttrId());
            if (Objects.isNull(attr)) {
                return;
            }
            if (!elastic.getIsEphedrine()) {
                elastic.setIsEphedrine(Compute.isEphedrine(attr));
            }
        });
    }
}
