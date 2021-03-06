package cn.wr.collect.sync.service;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.constants.ElasticEnum;
import cn.wr.collect.sync.constants.SymbolConstants;
import cn.wr.collect.sync.dao.gc.GoodsManagementDao;
import cn.wr.collect.sync.dao.gc.GoodsManagementStoreDao;
import cn.wr.collect.sync.dao.price.PriceListDAO;
import cn.wr.collect.sync.dao.price.PriceListDetailsDAO;
import cn.wr.collect.sync.dao.price.PriceStoreDAO;
import cn.wr.collect.sync.function.Compute;
import cn.wr.collect.sync.hbase.HBaseService;
import cn.wr.collect.sync.model.*;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.gc.*;
import cn.wr.collect.sync.model.partner.PartnerGoods;
import cn.wr.collect.sync.model.partner.PartnerGoodsInfo;
import cn.wr.collect.sync.model.partner.PartnerStoreGoods;
import cn.wr.collect.sync.model.price.PriceList;
import cn.wr.collect.sync.model.price.PriceListDetails;
import cn.wr.collect.sync.model.price.PriceStore;
import cn.wr.collect.sync.model.stock.StockGoods;
import cn.wr.collect.sync.model.stock.StockMerchant;
import cn.wr.collect.sync.redis.RedisService;
import cn.wr.collect.sync.utils.HBaseUtils;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.wr.collect.sync.constants.CommonConstants.*;

public class MultiFieldService {
    private static final Logger log = LoggerFactory.getLogger(MultiFieldService.class);
    private final RedisService redisService;
    private final HBaseService hBaseService;
    private final ParameterTool tool;

    public MultiFieldService(ParameterTool tool) {
        this.redisService = new RedisService(tool);
        this.hBaseService = new HBaseService(HBaseUtils.getConnection(tool));
        this.tool = tool;
    }

    /**
     * es ??????????????????
     * @param table
     * @param data
     * @param goods
     * @return
     */
    public ElasticO2O transferO2O(ElasticO2O o2o, String operate, Table.BaseDataTable table, Model data, PartnerGoods goods) {
        switch (table) {
            case gc_goods_manual:
                BaseGoods bg = redisService.queryBaseGoods(((GoodsManual) data).getApprovalNumber());
                if (StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                        || StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate)) {
                    o2o.setIndications(Compute.indications((GoodsManual) data));
                    o2o.setDrugName(Compute.drugName((GoodsManual) data));
                    o2o.setRelativeSickness(Compute.relativeSickness((GoodsManual) data));
                    o2o.setCommonName(Compute.commonName((GoodsManual) data, bg, goods));
                }
                else {
                    o2o.setCommonName(Compute.commonName(null, bg, goods));
                }

                break;

            case base_goods:
                GoodsManual manual = redisService.queryGcGoodsManual(((BaseGoods) data).getApprovalNumber());
                if (StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                        || StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate)) {
                    o2o.setCommonName(Compute.commonName(manual, (BaseGoods) data, goods));
                }
                else {
                    o2o.setCommonName(Compute.commonName(manual, null, goods));
                }
                break;

            case gc_partner_goods_gift:
                if (StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                        || StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate)) {
                    if (Objects.nonNull(data) && Objects.nonNull(data.getId())) {
                        o2o.setIsOffShelf(true);
                    } else {
                        return null;
                    }
                }
                break;

            case gc_standard_goods_syncrds:
                if (StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                        || StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate)) {
                    o2o.setIsStandard(true);
                    o2o.setBrand(Compute.brand((StandardGoodsSyncrds) data));
                    o2o.setSearchKeywords(Compute.searchKeywords((StandardGoodsSyncrds) data));

                    o2o.setTradeCode(Compute.tradeCodeLess8(((StandardGoodsSyncrds) data)));
                    o2o.setStandardGoodsStatus(Compute.standardGoodsStatus(((StandardGoodsSyncrds) data)));

                    // attrs
                    this.transferO2OFieldAttrs(o2o, ((StandardGoodsSyncrds) data).getTradeCode());
                    // cates
                    o2o.setCateIds(Compute.cateIds(this.transferO2OFieldCates(((StandardGoodsSyncrds) data).getTradeCode())));
                }
                else {
                    o2o.setIsStandard(false);
                    o2o.setBrand(null);
                    o2o.setSearchKeywords(null);
                    o2o.setTradeCode(null);
                    o2o.setIsEphedrine(false);
                    o2o.setIsDouble(false);
                    o2o.setStandardGoodsStatus(false);
                    o2o.setAttrIds(Collections.emptyList());
                    o2o.setCateIds(Collections.emptyList());
                }

                break;

            case gc_goods_spu_attr_syncrds:
            case gc_goods_attr_info_syncrds:
                // ??????????????????
                /*StandardGoodsSyncrds standard03 = redisService.queryGcStandardGoodsSyncrds(goods.getTradeCode());
                if (Objects.isNull(standard03)) {
                    return null;
                }
                this.transferO2OFieldAttrs(o2o, standard03.getTradeCode());*/
                break;

            case partner_goods_img:
                /*if ((StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                        || StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate))
                    && (Objects.nonNull(data) && StringUtils.isNotBlank(((PartnerGoodsImg) data).getImg()))) {
                    o2o.setImg(((PartnerGoodsImg) data).getImg());
                }
                else {
                    StandardGoodsSyncrds goodsSyncrds01 = redisService.queryGcStandardGoodsSyncrds(goods.getTradeCode());
                    if (Objects.nonNull(goodsSyncrds01) && StringUtils.isNotBlank(goodsSyncrds01.getUrls())) {
                        o2o.setImg(goodsSyncrds01.getUrls());
                    } else {
                        BaseSpuImg spuImg01 = redisService.queryGcBaseSpuImg(this.getRealApprovalNumber(goods));
                        if (Objects.nonNull(spuImg01) && StringUtils.isNotBlank(spuImg01.getPic())) {
                            o2o.setImg(spuImg01.getPic());
                        } else {
                            PartnerGoodsInfo goodsInfo01 = redisService.queryPartnerGoodsInfo(goods.getDbId(), goods.getInternalId());
                            if (Objects.nonNull(goodsInfo01) && StringUtils.isNotBlank(goodsInfo01.getImages())) {
                                o2o.setImg(goodsInfo01.getImages());
                            }
                        }
                    }
                }*/
                break;

            case gc_base_spu_img:
                /*PartnerGoodsImg goodsImg03 = redisService.queryPartnerGoodsImg(goods.getDbId(), goods.getInternalId());
                if (Objects.nonNull(goodsImg03) && StringUtils.isNotBlank(goodsImg03.getImg())) {
                    o2o.setImg(goodsImg03.getImg());
                } else {
                    StandardGoodsSyncrds goodsSyncrds03 = redisService.queryGcStandardGoodsSyncrds(goods.getTradeCode());
                    if (Objects.nonNull(goodsSyncrds03) && StringUtils.isNotBlank(goodsSyncrds03.getUrls())) {
                        o2o.setImg(goodsSyncrds03.getUrls());
                    } else {
                        if ((StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                                || StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate))
                            && (Objects.nonNull(data) && StringUtils.isNotBlank(((BaseSpuImg) data).getPic()))) {
                            o2o.setImg(((BaseSpuImg) data).getPic());
                        } else {
                            PartnerGoodsInfo goodsInfo = redisService.queryPartnerGoodsInfo(goods.getDbId(), goods.getInternalId());
                            if (Objects.nonNull(goodsInfo) && StringUtils.isNotBlank(goodsInfo.getImages())) {
                                o2o.setImg(goodsInfo.getImages());
                            }
                        }
                    }
                }*/
                break;

            case partner_goods_info:
                /*PartnerGoodsImg goodsImg04 = redisService.queryPartnerGoodsImg(goods.getDbId(), goods.getInternalId());
                if (Objects.nonNull(goodsImg04) && StringUtils.isNotBlank(goodsImg04.getImg())) {
                    o2o.setImg(goodsImg04.getImg());
                } else {
                    StandardGoodsSyncrds goodsSyncrds04 = redisService.queryGcStandardGoodsSyncrds(goods.getTradeCode());
                    if (Objects.nonNull(goodsSyncrds04) && StringUtils.isNotBlank(goodsSyncrds04.getUrls())) {
                        o2o.setImg(goodsSyncrds04.getUrls());
                    } else {
                        BaseSpuImg spuImg = redisService.queryGcBaseSpuImg(this.getRealApprovalNumber(goods));
                        if (Objects.nonNull(spuImg) && StringUtils.isNotBlank(spuImg.getPic())) {
                            o2o.setImg(spuImg.getPic());
                        } else {

                            // ??????/?????? ????????? ????????????
                            // ?????? ?????????
                            if (StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                                    || StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate)) {
                                if (StringUtils.isNotBlank(((PartnerGoodsInfo) data).getImages())) {
                                    o2o.setImg(((PartnerGoodsInfo) data).getImages());
                                }
                            }
                        }
                    }
                }*/
                break;

            case gc_goods_spu:
                // ???????????? approval_number ???????????????
                /*if (StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                        || StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate)) {
                    this.transferO2OAboutApprovalNum(o2o, ((GoodsSpu) data).getApprovalNumber(), goods);
                }
                else {
                    this.transferO2OAboutApprovalNum(o2o, null, goods);
                }*/
                break;

            case gc_goods_cate_spu:
            case gc_category_info:
                /*o2o.setCateIds(Compute.cateIds(this.transferO2OFieldCates(goods.getTradeCode())));*/
                break;

            case gc_config_sku:
                if (StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                        || StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate)) {
                    o2o.setGoodsType(Compute.goodsType((ConfigSku) data));
                    o2o.setGoodsSubType(Compute.goodsSubType((ConfigSku) data));
                    o2o.setIsStandardOffShelf(Compute.isStandardOffShelf((ConfigSku) data));
                    o2o.setSkuAuditStatus(Compute.skuAuditStatus((ConfigSku) data));
                }
                else {
                    o2o.setIsStandardOffShelf(Compute.isStandardOffShelf(null));
                    o2o.setSkuAuditStatus(Compute.skuAuditStatus(null));
                }
                break;

            case organize_base:
                // netType????????????????????????????????????????????????????????????????????????????????????
                if (StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                        || StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate)) {
                    o2o.setIsDtpStore(Compute.isDtpStore((OrganizeBase) data));
                }
                else {
                    o2o.setIsDtpStore(Compute.isDtpStore(null));
                }
                break;

            default:
                return null;
        }
        return o2o;
    }

    /**
     * ??????????????????
     * @param table
     * @param m
     * @param clone
     */
    public boolean specialFields(Table.BaseDataTable table, PartnerGoods goods, SplitMiddleData m, ElasticO2O clone) {
        switch (table) {
            case base_goods:
                if (StringUtils.isBlank(clone.getCommonName())) {
                    clone.setCommonName(m.getCommonName());
                }
                break;

            case gc_standard_goods_syncrds:
                // ????????? gc_standard_goods_syncrds ??? ??????/??????/spu_id????????????
                this.transferO2OAboutApprovalNum(clone, this.getStandardApprovalNumber(clone.getMerchantId(), clone.getGoodsInternalId(),
                        clone.getRealTradeCode(), clone.getApprovalNumber()), goods);

                PartnerGoodsImg standardGoodsImg = redisService.queryPartnerGoodsImg(m.getDbId(), m.getInternalId());
                if (Objects.nonNull(standardGoodsImg) && StringUtils.isNotBlank(standardGoodsImg.getImg())) {
                    clone.setImg(standardGoodsImg.getImg());
                }
                else {
                    // ??????????????????
                    ConfigSku configSkuImg = hBaseService.queryConfigSku(m.getMerchantId(), m.getInternalId());
                    StandardGoodsSyncrds standardImg = redisService.queryGcStandardGoodsSyncrds(goods.getTradeCode());
                    // ???????????????????????????
                    String standardTradeCodeImg = Compute.tradeCode(configSkuImg, standardImg);
                    StandardGoodsSyncrds goodsSyncrds01 = redisService.queryGcStandardGoodsSyncrds(standardTradeCodeImg);
                    if (Objects.nonNull(goodsSyncrds01) && StringUtils.isNotBlank(goodsSyncrds01.getUrls())) {
                        clone.setImg(goodsSyncrds01.getUrls());
                    } else {
                        BaseSpuImg spuImg01 = redisService.queryGcBaseSpuImg(this.getStandardApprovalNumber(standardTradeCodeImg, m.getApprovalNumber()));
                        if (Objects.nonNull(spuImg01) && StringUtils.isNotBlank(spuImg01.getPic())) {
                            clone.setImg(spuImg01.getPic());
                        } else {
                            PartnerGoodsInfo goodsInfo01 = redisService.queryPartnerGoodsInfo(m.getDbId(), m.getInternalId());
                            if (Objects.nonNull(goodsInfo01) && StringUtils.isNotBlank(goodsInfo01.getImages())) {
                                clone.setImg(goodsInfo01.getImages());
                            }
                        }
                    }
                }

                if (StringUtils.isBlank(clone.getTradeCode())) {
                    clone.setTradeCode(m.getMerchantId() + m.getInternalId());
                }
                break;

            /*case gc_partner_goods_gift:
                PartnerGoodsGift gift = redisService.queryGcPartnerGoodsGift(m.getDbId(), m.getInternalId());
                clone.setIsOffShelf(Compute.isOffShelf(gift, m.getIsOffShelf()));
                break;*/

            case gc_goods_spu_attr_syncrds:
            case gc_goods_attr_info_syncrds:
                // ??????????????????
                ConfigSku configSku = hBaseService.queryConfigSku(m.getMerchantId(), m.getInternalId());
                StandardGoodsSyncrds standard = redisService.queryGcStandardGoodsSyncrds(m.getTradeCode());
                // ???????????????????????????
                String standardTradeCode = Compute.tradeCode(configSku, standard);
                StandardGoodsSyncrds standard03 = redisService.queryGcStandardGoodsSyncrds(standardTradeCode);
                if (Objects.isNull(standard03)) {
                    return false;
                }
                this.transferO2OFieldAttrs(clone, standard03.getTradeCode());
                break;

            case partner_goods_img:
            case gc_base_spu_img:
            case partner_goods_info:
                PartnerGoodsImg goodsImg = redisService.queryPartnerGoodsImg(m.getDbId(), m.getInternalId());
                if (Objects.nonNull(goodsImg) && StringUtils.isNotBlank(goodsImg.getImg())) {
                    clone.setImg(goodsImg.getImg());
                }
                else {
                    // ??????????????????
                    ConfigSku configSkuImg = hBaseService.queryConfigSku(m.getMerchantId(), m.getInternalId());
                    StandardGoodsSyncrds standardImg = redisService.queryGcStandardGoodsSyncrds(m.getTradeCode());
                    // ???????????????????????????
                    String standardTradeCodeImg = Compute.tradeCode(configSkuImg, standardImg);
                    StandardGoodsSyncrds goodsSyncrds01 = redisService.queryGcStandardGoodsSyncrds(standardTradeCodeImg);
                    if (Objects.nonNull(goodsSyncrds01) && StringUtils.isNotBlank(goodsSyncrds01.getUrls())) {
                        clone.setImg(goodsSyncrds01.getUrls());
                    } else {
                        BaseSpuImg spuImg01 = redisService.queryGcBaseSpuImg(this.getStandardApprovalNumber(standardTradeCodeImg, m.getApprovalNumber()));
                        if (Objects.nonNull(spuImg01) && StringUtils.isNotBlank(spuImg01.getPic())) {
                            clone.setImg(spuImg01.getPic());
                        } else {
                            PartnerGoodsInfo goodsInfo01 = redisService.queryPartnerGoodsInfo(m.getDbId(), m.getInternalId());
                            if (Objects.nonNull(goodsInfo01) && StringUtils.isNotBlank(goodsInfo01.getImages())) {
                                clone.setImg(goodsInfo01.getImages());
                            }
                        }
                    }
                }
                break;

            case gc_goods_dosage:
            case gc_base_nootc:
                String standardTradeCodePre = this.getStandardTradeCode(m.getMerchantId(), m.getInternalId(), m.getTradeCode());
                GoodsDosage dosage = redisService.queryGcGoodsDosage(standardTradeCodePre);
                BaseNootc baseNootc = redisService.queryGcBaseNootc(this.getStandardApprovalNumber(standardTradeCodePre, m.getApprovalNumber()));
                clone.setIsPrescription(Compute.isPrescription(baseNootc, dosage));
                break;

            case gc_goods_spu:
                String approvalNumber = this.getStandardApprovalNumber(m.getMerchantId(), m.getInternalId(), m.getTradeCode(), m.getApprovalNumber());
                this.transferO2OAboutApprovalNum(clone, approvalNumber, goods);
                break;

            case gc_goods_cate_spu:
            case gc_category_info:
                String standardTradeCodeCate = this.getStandardTradeCode(m.getMerchantId(), m.getInternalId(), m.getTradeCode());
                clone.setCateIds(Compute.cateIds(this.transferO2OFieldCates(standardTradeCodeCate)));
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * ???????????? is_ephedrine/is_double/attrs
     * @param o2o
     * @param tradeCode
     */
    public void transferO2OFieldAttrs(ElasticO2O o2o, String tradeCode) {
        o2o.setIsEphedrine(false);
        o2o.setIsDouble(false);
        List<GoodsSpuAttrSyncrds> spuAttrList = redisService.queryGcGoodsSpuAttrSyncrds(tradeCode);
        Set<Long> attrs = new HashSet<>();
        spuAttrList.forEach(spuAttr -> {
            GoodsAttrInfoSyncrds attr = redisService.queryGcGoodsAttrInfoSyncrds(spuAttr.getAttrId());
            if (Objects.isNull(attr)) {
                return;
            }
            if (!o2o.getIsEphedrine()) {
                o2o.setIsEphedrine(Compute.isEphedrine(attr));
            }
            if (!o2o.getIsDouble()) {
                o2o.setIsDouble(Compute.isDouble(attr));
            }

            List<Long> attrList = Compute.attrs(attr);
            if (CollectionUtils.isNotEmpty(attrList)) {
                attrs.addAll(attrList);
            }
        });
        o2o.setAttrIds(Compute.attrIds(new ArrayList<>(attrs)));
        // o2o.setIsDtp(Compute.isDtpLong(attrs));
    }


    /**
     * ???????????? attrs
     * @param tradeCode
     * @return
     */
    private List<Long> transferO2OFieldCates(String tradeCode) {
        // ??????
        StandardGoodsSyncrds standard = redisService.queryGcStandardGoodsSyncrds(tradeCode);
        if (Objects.isNull(standard)) {
            return Collections.emptyList();
        }
        List<GoodsCateSpu> cateSpuList = redisService.queryGoodsCateSpu(tradeCode);
        if (CollectionUtils.isEmpty(cateSpuList)) {
            return Collections.emptyList();
        }
        Set<Long> cateSet = cateSpuList.stream()
                .filter(cs -> Objects.nonNull(cs) && Objects.nonNull(cs.getCateId()))
                .flatMap(cs -> {
                    CategoryInfo cate = redisService.queryCategoryInfo(cs.getCateId());
                    if (Objects.isNull(cate) || !CATE_DELETED_ENABLE.equals(cate.getDeleted())) {
                        return null;
                    }
                    List<Long> cates = new ArrayList<>();
                    cates.add(cate.getId());
                    if (StringUtils.isNotBlank(cate.getPids())) {
                        String[] split = cate.getPids().split(SymbolConstants.COMMA_EN);
                        if (split.length > 0) {
                            cates.addAll(Arrays.stream(split).filter(StringUtils::isNotBlank)
                                    .map(Long::valueOf).collect(Collectors.toList()));
                        }
                    }
                    return cates.stream();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return CollectionUtils.isNotEmpty(cateSet) ? new ArrayList<>(cateSet) : Collections.emptyList();
    }

    /**
     * ??????approvalNumber
     * @param merchantId
     * @param internalId
     * @param tradeCode
     * @param approvalNumber
     * @return
     */
    public String getStandardApprovalNumber(Integer merchantId, String internalId, String tradeCode, String approvalNumber) {

        // ???????????????????????????
        String standardTradeCode = this.getStandardTradeCode(merchantId, internalId, tradeCode);
        return this.getStandardApprovalNumber(standardTradeCode, approvalNumber);
    }

    /**
     * ??????approvalNumber
     * @param standardTradeCode
     * @param approvalNumber
     * @return
     */
    public String getStandardApprovalNumber(String standardTradeCode, String approvalNumber) {
        if (StringUtils.isBlank(standardTradeCode)) {
            return approvalNumber;
        }
        StandardGoodsSyncrds standard = redisService.queryGcStandardGoodsSyncrds(standardTradeCode);
        if (Objects.isNull(standard) || Objects.isNull(standard.getSpuId())) {
            return approvalNumber;
        }
        GoodsSpu goodsSpu = redisService.queryGoodsSpu(standard.getSpuId());
        return Compute.approvalNumber(approvalNumber, goodsSpu);
    }

    /**
     * ??????????????????
     * @param configSku ????????????
     * @param tradeCode
     * @return
     */
    public String getStandardTradeCode(ConfigSku configSku, String tradeCode) {
        // gc_standard_goods ??????????????????????????????????????????????????????
        StandardGoodsSyncrds standardGoodsSyncrds = redisService.queryGcStandardGoodsSyncrds(tradeCode);
        // ?????????????????? gc_config_sku/gc_standard_goods
        return Compute.tradeCode(configSku, standardGoodsSyncrds);
    }

    /**
     * ??????????????????
     * @param merchantId        ??????id
     * @param goodsInternalId   ????????????
     * @param tradeCode         ????????????
     * @return
     */
    public String getStandardTradeCode(Integer merchantId, String goodsInternalId, String tradeCode) {
        // gc_config_sku
        ConfigSku configSku = hBaseService.queryConfigSku(merchantId, goodsInternalId);
        return this.getStandardTradeCode(configSku, tradeCode);
    }

    /**
     * es ??????????????? approval_number ??????????????????
     * @param o2o
     * @param approvalNumber
     * @param goods
     */
    public void transferO2OAboutApprovalNum(ElasticO2O o2o, String approvalNumber, PartnerGoods goods) {
        log.info("MultiFieldService transferO2OAboutApprovalNum elastic:{} \n approvalNumber:{} \n goods:{}",
                JSON.toJSONString(o2o), approvalNumber, JSON.toJSONString(goods));
        if (StringUtils.isBlank(approvalNumber)) {
            approvalNumber = goods.getApprovalNumber();
        }
        o2o.setApprovalNumber(approvalNumber);
        // indications drug_name relative_sickness common_name
        BaseGoods baseGoods = redisService.queryBaseGoods(approvalNumber);
        GoodsManual goodsManual = redisService.queryGcGoodsManual(approvalNumber);
        o2o.setIndications(Compute.indications(goodsManual));
        o2o.setDrugName(Compute.drugName(goodsManual));
        o2o.setRelativeSickness(Compute.relativeSickness(goodsManual));
        o2o.setCommonName(Compute.commonName(goodsManual, baseGoods, goods));

        // is_prescription
        String standardTradeCode = this.getStandardTradeCode(o2o.getMerchantId(), o2o.getGoodsInternalId(), goods.getTradeCode());
        BaseNootc baseNootc = redisService.queryGcBaseNootc(approvalNumber);
        GoodsDosage dosage = redisService.queryGcGoodsDosage(standardTradeCode);
        o2o.setIsPrescription(Compute.isPrescription(baseNootc, dosage));

        // img
        PartnerGoodsImg goodsImg = redisService.queryPartnerGoodsImg(goods.getDbId(), goods.getInternalId());
        if (Objects.nonNull(goodsImg) && StringUtils.isNotBlank(goodsImg.getImg())) {
            o2o.setImg(goodsImg.getImg());
        } else {
            StandardGoodsSyncrds goodsSyncrds03 = redisService.queryGcStandardGoodsSyncrds(standardTradeCode);
            if (Objects.nonNull(goodsSyncrds03) && StringUtils.isNotBlank(goodsSyncrds03.getUrls())) {
                o2o.setImg(goodsSyncrds03.getUrls());
            } else {
                BaseSpuImg baseSpuImg = redisService.queryGcBaseSpuImg(approvalNumber);
                if ((Objects.nonNull(baseSpuImg) && StringUtils.isNotBlank(baseSpuImg.getPic()))) {
                    o2o.setImg(baseSpuImg.getPic());
                } else {
                    PartnerGoodsInfo goodsInfo = redisService.queryPartnerGoodsInfo(goods.getDbId(), goods.getInternalId());
                    if (Objects.nonNull(goodsInfo) && StringUtils.isNotBlank(goodsInfo.getImages())) {
                        o2o.setImg(goodsInfo.getImages());
                    }
                }
            }
        }
    }

    /**
     * ??????goods?????????es??????
     * @param operate
     * @param goods
     * @return
     */
    public ElasticO2O transferO2OByGoods(String operate, String tableName, PartnerGoods goods) {
        ElasticO2O o2o = new ElasticO2O();
        o2o.setRealTradeCode(Compute.realTradeCode(goods));
        if (GOODSFILTER.contains(tableName)
                && (StringUtils.equals(OPERATE_UPDATE_DELETE, operate) || StringUtils.equals(OPERATE_DELETE, operate))) {
            return o2o;
        }
        // is_off_shelf
        /*PartnerGoodsGift goodsGift = redisService.queryGcPartnerGoodsGift(o2o.getDbId(), o2o.getGoodsInternalId());
        o2o.setIsOffShelf(Compute.isOffShelf(goodsGift, o2o.getIsOffShelf()));*/

        o2o.setDbId(Compute.dbId(goods));
        o2o.setGoodsInternalId(Compute.goodsInternalId(goods));
        o2o.setApprovalNumber(Compute.approvalNumber(goods, null));
        //base_price????????????????????????
//        o2o.setBasePrice(Compute.basePrice(goods));
        o2o.setRealCommonName(Compute.realCommonName(goods));
        o2o.setForm(Compute.form(goods));
        o2o.setManufacturer(Compute.manufacturer(goods));
        o2o.setPack(Compute.pack(goods));

        /*StandardGoodsSyncrds standard = redisService.queryGcStandardGoodsSyncrds(o2o.getRealTradeCode());
        o2o.setTradeCode(Compute.tradeCode(standard, o2o.getMerchantId(), o2o.getGoodsInternalId()));
        o2o.setIsStandard(Compute.isStandard(standard));
        o2o.setBrand(Compute.brand(standard));
        o2o.setSearchKeywords(Compute.searchKeywords(standard));
        o2o.setStandardGoodsStatus(Compute.standardGoodsStatus(standard));

        o2o.setIsEphedrine(false);
        o2o.setIsDouble(false);
        o2o.setAttrIds(Collections.emptyList());
        if (Objects.nonNull(standard)) {
            if (Objects.nonNull(standard.getSpuId())) {
                // approval_number
                GoodsSpu goodsSpu = redisService.queryGoodsSpu(standard.getSpuId());
                o2o.setApprovalNumber(Compute.approvalNumber(goods, goodsSpu));
            }

            // attrs
            this.transferO2OFieldAttrs(o2o, standard.getTradeCode());
        }

        // is_dtp
        o2o.setIsDtp(Compute.isDtp(o2o.getAttrIds()));

        // cates
        o2o.setCateIds(Compute.cateIds(this.transferO2OFieldCates(o2o.getRealTradeCode())));

        // merchant_goods_category_mapping
        *//*MerchantGoodsCategoryMapping mgcm = redisService.queryMerchantGoodsCategoryMapping(o2o.getRealTradeCode());
        o2o.setCategoryFrontend(Compute.categoryFrontend(mgcm));*//*

        // GoodsManual
        GoodsManual manual = redisService.queryGcGoodsManual(o2o.getApprovalNumber());
        o2o.setIndications(Compute.indications(manual));
        o2o.setDrugName(Compute.drugName(manual));
        o2o.setRelativeSickness(Compute.relativeSickness(manual));
        *//*o2o.setEnName(Compute.enName(manual));
        o2o.setPinyinName(Compute.pinyinName(manual));
        o2o.setCureDisease(Compute.cureDisease(manual));
        o2o.setPediatricUse(Compute.pediatricUse(manual));
        o2o.setGeriatricUse(Compute.geriatricUse(manual));
        o2o.setPregnancyAndNursingMothers(Compute.pregnancyAndNursingMothers(manual));
        o2o.setOverDosage(Compute.overDosage(manual));*//*

        // BaseGoods
        BaseGoods baseGoods = redisService.queryBaseGoods(o2o.getApprovalNumber());
        o2o.setCommonName(Compute.commonName(manual, baseGoods, goods));
        *//*o2o.setCategoryOne(Compute.categoryOne(baseGoods));
        o2o.setCategoryTwo(Compute.categoryTwo(baseGoods));
        o2o.setCategoryThree(Compute.categoryThree(baseGoods));
        o2o.setCategoryFour(Compute.categoryFour(baseGoods));
        o2o.setCategoryFive(Compute.categoryFive(baseGoods));*//*

        // is_prescription
        GoodsDosage dosage = redisService.queryGcGoodsDosage(o2o.getRealTradeCode());
        BaseNootc nootc = redisService.queryGcBaseNootc(o2o.getApprovalNumber());
        *//*o2o.setDrugType(Compute.drugType(nootc));*//*
        o2o.setIsPrescription(Compute.isPrescription(nootc, dosage));

        // is_overweight: ??????0 ?????????
        GoodsOverweight goodsOverweight = redisService.queryGoodsOverweight(o2o.getRealTradeCode());
        o2o.setIsOverweight(Compute.isOverweight(goodsOverweight));

        // img????????????????????????????????? partner_goods_img???gc_standard_goods_syncrds???gc_base_spu_img
        // ???????????????????????????wr_partner.partner_goods_info?????????
        PartnerGoodsImg goodsImg = redisService.queryPartnerGoodsImg(o2o.getDbId(), o2o.getGoodsInternalId());
        o2o.setImg(Compute.img(goodsImg));
        if (StringUtils.isBlank(o2o.getImg())) {
            o2o.setImg(Compute.img(standard));
        }
        if (StringUtils.isBlank(o2o.getImg())) {
            BaseSpuImg baseSpuImg = redisService.queryGcBaseSpuImg(o2o.getApprovalNumber());
            o2o.setImg(Compute.img(baseSpuImg));
        }
        if (StringUtils.isBlank(o2o.getImg())) {
            PartnerGoodsInfo goodsInfo = redisService.queryPartnerGoodsInfo(o2o.getDbId(), o2o.getGoodsInternalId());
            o2o.setImg(Compute.img(goodsInfo));
        }

        // full_sales_volume: ???????????????
        GoodsFullSales goodsFullSales = hBaseService.queryGoodsFullSales(o2o.getRealTradeCode());
        o2o.setFullSalesVolume(Compute.fullSalesVolume(goodsFullSales));*/

        return o2o;
    }

    /**
     * ??????es????????????
     * @param operate
     * @param tableName
     * @param o2o
     * @param stores
     * @param goods
     * @param storeGoods
     */
    public void completeO2O(String operate, String tableName, ElasticO2O o2o, PartnerStoresAll stores,
                                   PartnerGoods goods, PartnerStoreGoods storeGoods) {
        if (Objects.isNull(o2o) || Objects.isNull(stores) || Objects.isNull(goods)) {
            return ;
        }

        // ?????????????????????????????????/??????????????? ??????kafka???????????????????????????????????????????????????????????????????????????
        // sku_code  ??????????????????store_goods???????????????
        o2o.setSkuCode(Compute.skuCode(stores, goods));
        // channel
        o2o.setChannel(Compute.channel(stores));
        // merchant_id
        o2o.setMerchantId(Compute.merchantId(stores));
        // store_id
        o2o.setStoreId(Compute.storeId(stores));
        // location: ?????????
        PgcStoreInfoIncrement storeIncre = redisService.queryPgcStoreInfoIncrement(stores.getMerchantId(), stores.getStoreId());
        o2o.setLocation(Compute.location(storeIncre));

        // control_status
//        String configSkuNo = o2o.getMerchantId() + "-" + o2o.getGoodsInternalId();
//        ConfigSkuSourceDAO configSkuSourceDAO = new ConfigSkuSourceDAO(tool);
//        ConfigSkuSource configSkuSource = configSkuSourceDAO.queryOne(configSkuNo);
        String managementSkuNo = o2o.getMerchantId() + "-" + o2o.getGoodsInternalId();
        GoodsManagementDao goodsManagementDao = new GoodsManagementDao(tool);
        GoodsManagement goodsManagement = goodsManagementDao.queryOne(managementSkuNo);

        //????????????????????????????????????0?????????????????? ??????
        if (Objects.isNull(goodsManagement) || goodsManagement.getOnlineDisableStatus() == 0) {
            o2o.setControlStatus(false);
        } else if (Objects.nonNull(goodsManagement) && goodsManagement.getOnlineDisableStatus() == 2) {
            //?????????????????????
            o2o.setControlStatus(true);
        } else if (Objects.nonNull(goodsManagement) && goodsManagement.getOnlineDisableStatus() == 1) {

            //?????????????????????
            String stockNo = o2o.getMerchantId() + "-" + o2o.getGoodsInternalId() + "-" + o2o.getStoreId();
            GoodsManagementStoreDao goodsManagementStoreDao = new GoodsManagementStoreDao(tool);
            GoodsManagementStore goodsManagementStore = goodsManagementStoreDao.queryOne(stockNo);

            if (Objects.isNull(goodsManagementStore)) {
                //??????????????????
                o2o.setControlStatus(false);
            } else {
                //???????????????
                o2o.setControlStatus(true);
            }
        }

        // sync_date
        o2o.setSyncDate(LocalDateTime.now());

        // gc_config_sku
        ConfigSku configSku = hBaseService.queryConfigSku(o2o.getMerchantId(), o2o.getGoodsInternalId());
        // ?????????????????? gc_config_sku/gc_standard_goods
        String standardTradeCode = this.getStandardTradeCode(configSku, o2o.getRealTradeCode());

        // trade_code
        o2o.setTradeCode(Compute.tradeCodeLess8(standardTradeCode, o2o.getMerchantId(), o2o.getGoodsInternalId()));

        // is_standard_off_shelf
        o2o.setIsStandardOffShelf(Compute.isStandardOffShelf(configSku));

        // is_sku_audited
        o2o.setSkuAuditStatus(Compute.skuAuditStatus(configSku));

        // partner_goods / partner_store_goods ?????????????????????/??????????????? ???????????? sku_code
        if (GOODSFILTER.contains(tableName)
                && (StringUtils.equals(OPERATE_UPDATE_DELETE, operate) || StringUtils.equals(OPERATE_DELETE, operate))) {
            return ;
        }

        StandardGoodsSyncrds standard = redisService.queryGcStandardGoodsSyncrds(standardTradeCode);

        // goods_type/ goods_sub_type
        o2o.setGoodsSubType(Compute.goodsSubType(configSku));
        o2o.setGoodsType(Compute.goodsType(configSku));

        o2o.setIsStandard(Compute.isStandard(standard));
        o2o.setBrand(Compute.brand(standard));
        o2o.setSearchKeywords(Compute.searchKeywords(standard));
        o2o.setStandardGoodsStatus(Compute.standardGoodsStatus(standard));

        o2o.setIsEphedrine(false);
        o2o.setIsDouble(false);
        o2o.setAttrIds(Collections.emptyList());
        if (Objects.nonNull(standard)) {
            if (Objects.nonNull(standard.getSpuId())) {
                // approval_number
                GoodsSpu goodsSpu = redisService.queryGoodsSpu(standard.getSpuId());
                o2o.setApprovalNumber(Compute.approvalNumber(goods, goodsSpu));
            }

            // attrs
            this.transferO2OFieldAttrs(o2o, standardTradeCode);
        }

        // is_dtp
        o2o.setIsDtp(Compute.isDtp(o2o.getAttrIds()));

        // stock_goods
        StockGoods stockGoods = hBaseService.queryStockGoods(Long.valueOf(stores.getMerchantId()),
                Long.valueOf(stores.getStoreId()), goods.getInternalId());
        o2o.setIsVirtualStock(Compute.isVirtualStock(stockGoods));
        o2o.setStockQuantity(Compute.stockQuantity(stockGoods));

        log.info("????????????????????????{}", o2o.getMerchantId());

        // is_off_shelf
        //?????????????????????
        StockMerchant stockMerchant = redisService.queryStockMerchant(o2o.getMerchantId(), o2o.getMerchantId());
        //????????????????????????????????????????????????????????????
        if (Objects.isNull(stockMerchant) || stockMerchant.getPriceState() == 0) {
            log.info("??????????????????????????????{}", stockMerchant);
            this.setPartnerPrice(o2o, stores, goods, storeGoods);
        } else {
            //?????????????????????????????????
            PriceStoreDAO priceStoreDAO = new PriceStoreDAO(tool);
            HashMap<String, Object> params = new HashMap<>();
            params.put("organizationId", Long.valueOf(String.valueOf(o2o.getMerchantId())));
            params.put("storeId", Long.valueOf(String.valueOf(o2o.getStoreId())));
            PriceStore priceStore = priceStoreDAO.queryOne(params);

            log.info("??????????????????????????????{}", priceStore);

            if (Objects.nonNull(priceStore) && StringUtils.isNotBlank(priceStore.getListId())
             && Objects.nonNull(o2o.getMerchantId()) && StringUtils.isNotBlank(o2o.getGoodsInternalId())) {
                PriceListDetails priceListDetails = this.getPriceListDetails(priceStore.getListId(), o2o.getMerchantId(), o2o.getGoodsInternalId());
                //????????????????????????????????????
                if (Objects.nonNull(priceListDetails)) {
                    log.info("?????????????????????{}", priceListDetails);
                    o2o.setSalePrice(priceListDetails.getSkuPrice());
                    o2o.setBasePrice(priceListDetails.getOriginalPrice());
                } else {
                    log.info("??????????????????????????????????????????, merchantId:{}, storeId:{}", o2o.getMerchantId(), o2o.getStoreId());
                    //????????????????????????????????????????????????
                    this.setPartnerPrice(o2o, stores, goods, storeGoods);
                }
            } else {
                log.info("????????????????????????????????????????????????, merchantId:{}, storeId:{}", o2o.getMerchantId(), o2o.getStoreId());
                //????????????????????????????????????????????????
                this.setPartnerPrice(o2o, stores, goods, storeGoods);
            }
        }


        //????????????????????????
        if (StringUtils.equals(ElasticEnum.B2C.getChannel(), stores.getChannel())) {
            o2o.setIsOffShelf(Compute.isOffShelf(stockGoods, goods, null));
        } else if (StringUtils.equals(ElasticEnum.O2O.getChannel(), stores.getChannel())) {
            o2o.setIsOffShelf(Compute.isOffShelf(stockGoods, goods, storeGoods));
        }

        // trade_code: ??????????????? trade_code = {merchant_id}{goods_internal_id}
        /*if (StringUtils.isBlank(o2o.getRealTradeCode())
                || !StringUtils.equals(o2o.getRealTradeCode(), o2o.getTradeCode())) {
            o2o.setTradeCode(Compute.tradeCode(null, stores.getMerchantId(), o2o.getGoodsInternalId()));
        }*/

        // store_status: ????????????
        OrganizeBase base = redisService.queryOrganizeBase(stores.getMerchantId(), stores.getStoreId());
        o2o.setStoreStatus(Compute.storeStatus(base));
        o2o.setIsDtpStore(Compute.isDtpStore(base));

        // (??????) && ???DTP??????
        if ((Objects.isNull(o2o.getIsOffShelf()) || o2o.getIsOffShelf())
                && !CommonConstants.IS_DTP_TRUE.equals(o2o.getIsDtp())) {
            return;
        }

        // cates
        o2o.setCateIds(Compute.cateIds(this.transferO2OFieldCates(standardTradeCode)));

        // GoodsManual
        GoodsManual manual = redisService.queryGcGoodsManual(o2o.getApprovalNumber());
        o2o.setIndications(Compute.indications(manual));
        o2o.setDrugName(Compute.drugName(manual));
        o2o.setRelativeSickness(Compute.relativeSickness(manual));


        // BaseGoods
        BaseGoods baseGoods = redisService.queryBaseGoods(o2o.getApprovalNumber());
        o2o.setCommonName(Compute.commonName(manual, baseGoods, goods));

        // is_prescription
//        GoodsDosage dosage = redisService.queryGcGoodsDosage(standardTradeCode);
//        BaseNootc nootc = redisService.queryGcBaseNootc(o2o.getApprovalNumber());
//        o2o.setIsPrescription(Compute.isPrescription(nootc, dosage));
        o2o.setIsPrescription(Compute.isPrescription(o2o.getAttrIds()));

        // is_overweight: ??????0 ?????????
        GoodsOverweight goodsOverweight = redisService.queryGoodsOverweight(o2o.getRealTradeCode());
        o2o.setIsOverweight(Compute.isOverweight(goodsOverweight));

        // img????????????????????????????????? partner_goods_img???gc_standard_goods_syncrds???gc_base_spu_img
        // ???????????????????????????wr_partner.partner_goods_info?????????
        PartnerGoodsImg goodsImg = redisService.queryPartnerGoodsImg(o2o.getDbId(), o2o.getGoodsInternalId());
        o2o.setImg(Compute.img(goodsImg));
        if (StringUtils.isBlank(o2o.getImg())) {
            o2o.setImg(Compute.img(standard));
        }
        if (StringUtils.isBlank(o2o.getImg())) {
            BaseSpuImg baseSpuImg = redisService.queryGcBaseSpuImg(o2o.getApprovalNumber());
            o2o.setImg(Compute.img(baseSpuImg));
        }
        if (StringUtils.isBlank(o2o.getImg())) {
            PartnerGoodsInfo goodsInfo = redisService.queryPartnerGoodsInfo(o2o.getDbId(), o2o.getGoodsInternalId());
            o2o.setImg(Compute.img(goodsInfo));
        }

        // full_sales_volume: ???????????????
        GoodsFullSales goodsFullSales = hBaseService.queryGoodsFullSales(o2o.getRealTradeCode());
        o2o.setFullSalesVolume(Compute.fullSalesVolume(goodsFullSales));

        // sales_volume: ??????
        GoodsSales goodsSales = hBaseService.queryGoodsSales(o2o.getMerchantId(), o2o.getGoodsInternalId());
        o2o.setSalesVolume(Compute.salesVolume(goodsSales));

        // is_wr_off_shelf: ???????????????
        PlatformGoods platformGoods = hBaseService.queryPlatformGoods(o2o.getSkuCode());
        o2o.setIswrOffShelf(Compute.iswrOffShelf(platformGoods));

        // province_code/province_name/city_code/city_name/area_code/area_name: ?????????
        PgcStoreInfo storeInfo = redisService.queryPgcStoreInfo(o2o.getMerchantId(), stores.getStoreId());
        o2o.setProvinceCode(Compute.provinceCode(storeInfo));
        o2o.setProvinceName(Compute.provinceName(storeInfo));
        o2o.setCityCode(Compute.cityCode(storeInfo));
        o2o.setCityName(Compute.cityName(storeInfo));
        o2o.setAreaCode(Compute.areaCode(storeInfo));
        o2o.setAreaName(Compute.areaName(storeInfo));

        // spell_word: ?????????
        SkuExtend skuExtend = redisService.querySkuExtend(o2o.getMerchantId(), o2o.getGoodsInternalId(), SPELL_WORD);
        o2o.setSpellWord(Compute.spellWord(skuExtend));
    }


    private PriceListDetails getPriceListDetails(String listId, Integer organizationId, String internalId) {
        //???????????????????????????
        PriceListDetailsDAO priceListDetailsDAO = new PriceListDetailsDAO(tool);
        HashMap<String, Object> detailsParams = new HashMap<>();
        detailsParams.put("organizationId", Long.valueOf(String.valueOf(organizationId)));
        detailsParams.put("listId", listId);
        detailsParams.put("internalId", internalId);
        PriceListDetails priceListDetails = priceListDetailsDAO.queryOne(detailsParams);

        //??????????????????????????????
        if (Objects.nonNull(priceListDetails)) {
            return priceListDetails;
        }

        //??????????????????????????????????????????????????????????????????id
        PriceListDAO priceListDAO = new PriceListDAO(tool);
        HashMap<String, Object> listParams = new HashMap<>();
        listParams.put("organizationId", Long.valueOf(String.valueOf(organizationId)));
        listParams.put("listId", listId);
        PriceList priceList = priceListDAO.queryOne(listParams);
        if (Objects.nonNull(priceList) && StringUtils.isNotBlank(priceList.getParentListId())) {
            this.getPriceListDetails(priceList.getParentListId(), organizationId, internalId);
        }

        return null;
    }


    private void setPartnerPrice(ElasticO2O o2o, PartnerStoresAll stores, PartnerGoods goods, PartnerStoreGoods storeGoods) {
        o2o.setBasePrice(Compute.basePrice(goods));
        if (StringUtils.equals(ElasticEnum.B2C.getChannel(), stores.getChannel())) {
            o2o.setSalePrice(Compute.salePrice(goods));
        } else if (StringUtils.equals(ElasticEnum.O2O.getChannel(), stores.getChannel())) {
            o2o.setSalePrice(Compute.salePrice(storeGoods));
        }
    }


    /**
     * ??????partner_goods???????????????????????????
     * @param tableName
     * @param operate
     * @param goods
     * @param collector
     */
    public void splitByBasicTimeGoods(String tableName, String operate, BasicTimeGoods goods, List<String> modFieldList,
                                       Collector<BasicModel<ElasticO2O>> collector) {
        if (null == goods.getDbId() || StringUtils.isBlank(goods.getInternalId())) {
            return;
        }
        List<PartnerStoresAll> list = redisService.queryPartnerStoresAll(goods.getDbId());
        if (CollectionUtils.isEmpty(list)) {
            return ;
        }
        if (Objects.nonNull(goods.getMerchantId())) {
            list = list.stream().filter(item -> goods.getMerchantId().equals(item.getMerchantId()))
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        if (Objects.nonNull(goods.getStoreId())) {
            list = list.stream().filter(item -> goods.getStoreId().equals(item.getStoreId()))
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<String> rowKeyList = list.stream().map(e -> (e.getDbId() + "-" + e.getGroupId() + "-" + goods.getInternalId()))
                .collect(Collectors.toList());
        List<PartnerStoreGoods> storeGoodsList = hBaseService.queryPartnerStoreGoods(rowKeyList);

        this.transferFromGoods2Collect(tableName, operate, modFieldList, goods, list, storeGoodsList, collector);
    }

    /**
     * ??????partner_goods???????????????????????????
     * @param tableName
     * @param operate
     * @param storeGoods
     * @param collector
     */
    public void splitByBasicTimeStoreGoods(String tableName, String operate, List<String> modFieldList,
                                           BasicTimeStoreGoods storeGoods, Collector<BasicModel<ElasticO2O>> collector) {
        if (Objects.isNull(storeGoods.getDbId()) || StringUtils.isBlank(storeGoods.getGroupId())
                || StringUtils.isBlank(storeGoods.getGoodsInternalId())) {
            return;
        }
        List<PartnerStoresAll> list = redisService.queryPartnerStoresAll(storeGoods.getDbId(), storeGoods.getGroupId());
        if (CollectionUtils.isEmpty(list)) {
            return ;
        }

        if (Objects.nonNull(storeGoods.getMerchantId())) {
            list = list.stream().filter(item -> storeGoods.getMerchantId().equals(item.getMerchantId()))
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        if (Objects.nonNull(storeGoods.getStoreId())) {
            list = list.stream().filter(item -> storeGoods.getStoreId().equals(item.getStoreId()))
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        // organize_base??????????????????????????? ???????????????????????????????????????
        /*if (StringUtils.equals(Table.BaseDataTable.organize_base.name(), tableName)) {
            OrganizeBase ob = redisService.queryOrganizeBase(storeGoods.getMerchantId(), storeGoods.getStoreId());
            Integer storeStatus = Compute.storeStatus(ob);
            if (StringUtils.equals(OPERATE_DELETE, operate) || StringUtils.equals(OPERATE_UPDATE_DELETE, operate) || !IS_O2O.equals(storeStatus)) {
                ElasticO2O o2o = new ElasticO2O();
                o2o.setSkuCode(Compute.skuCode(ob.getRootId(), ob.getOrganizationId(), list.get(0).getChannel(), storeGoods.getGoodsInternalId()));
                o2o.setStoreStatus(storeStatus);
                collector.collect(new BasicModel<>(tableName, operate, o2o, modFieldList));
                return;
            }
        }*/

        PartnerGoods goods = hBaseService.queryPartnerGoods(storeGoods.getDbId(), storeGoods.getGoodsInternalId());
        if (Objects.isNull(goods)) {
            return;
        }
        this.transferFromStoreGoods2Collect(tableName, operate, modFieldList, goods, list, storeGoods, collector);
    }


    /**
     * ??????partner_goods???????????????????????????
     * @param tableName
     * @param operate
     * @param goods
     * @param collector
     */
    public void splitByPartnerGoods(String tableName, String operate, List<String> modFieldList, PartnerGoods goods,
                                     Collector<BasicModel<ElasticO2O>> collector) {
        if (Objects.isNull(goods.getDbId()) || StringUtils.isBlank(goods.getInternalId())) {
            return;
        }
        List<PartnerStoresAll> list = redisService.queryPartnerStoresAll(goods.getDbId());
        if (CollectionUtils.isEmpty(list)) {
            return ;
        }

        // partner_goods/partner_store_goods ?????????????????????/??????????????? ???????????? sku_code
        if (GOODSFILTER.contains(tableName)
                && (StringUtils.equals(OPERATE_DELETE, operate) || StringUtils.equals(OPERATE_UPDATE_DELETE, operate))) {
            this.deleteByGoods(tableName, operate, goods, list, collector);
            return;
        }

        List<String> rowKeyList = list.stream().map(e -> (e.getDbId() + "-" + e.getGroupId() + "-" + goods.getInternalId()))
                .collect(Collectors.toList());

        List<PartnerStoreGoods> storeGoodsList = hBaseService.queryPartnerStoreGoods(rowKeyList);
        if (CollectionUtils.isEmpty(storeGoodsList)) {
            return;
        }

        this.transferFromGoods2Collect(tableName, operate, modFieldList, goods, list, storeGoodsList, collector);
    }

    /**
     * ??????partner_store_goods???????????????????????????
     * @param tableName
     * @param operate
     * @param storeGoods
     * @param collector
     */
    public void splitByPartnerStoreGoods(String tableName, String operate, List<String> modFieldList,
                                         PartnerStoreGoods storeGoods, Collector<BasicModel<ElasticO2O>> collector) {
        if (Objects.isNull(storeGoods.getDbId()) || StringUtils.isBlank(storeGoods.getGroupId())
                || StringUtils.isBlank(storeGoods.getGoodsInternalId())) {
            return;
        }

        List<PartnerStoresAll> list = redisService.queryPartnerStoresAll(storeGoods.getDbId(), storeGoods.getGroupId());
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        PartnerGoods goods = hBaseService.queryPartnerGoods(storeGoods.getDbId(), storeGoods.getGoodsInternalId());
        if (Objects.isNull(goods)) {
            return;
        }

        // partner_goods / partner_store_goods ?????????????????????????????????????????????????????????+???????????????sku_code?????????es
        if (GOODSFILTER.contains(tableName)
                && (StringUtils.equals(OPERATE_DELETE, operate) || StringUtils.equals(OPERATE_UPDATE_DELETE, operate))) {
            this.deleteByGoods(tableName, operate, goods, list, collector);
            return;
        }

        this.transferFromStoreGoods2Collect(tableName, operate, modFieldList, goods, list, storeGoods, collector);

    }

    /**
     * partner_store_goods ??? es ??????
     * @param tableName
     * @param operate
     * @param goods
     * @param list
     * @param storeGoodsList
     */
    private void transferFromGoods2Collect(String tableName, String operate, List<String> modFieldList,
                                           PartnerGoods goods,
                                           List<PartnerStoresAll> list,
                                           List<PartnerStoreGoods> storeGoodsList,
                                           Collector<BasicModel<ElasticO2O>> collector) {
        ElasticO2O o2o = this.transferO2OByGoods(operate, tableName, goods);
        if (Objects.isNull(o2o)) {
            return;
        }

        list.forEach(psa -> {
            ElasticO2O clone = null;
            if (StringUtils.equals(ElasticEnum.O2O.getChannel(), psa.getChannel())) {
                if (CollectionUtils.isEmpty(storeGoodsList)) {
                    return;
                }
                PartnerStoreGoods storeGoods = storeGoodsList.stream()
                        .filter(e -> psa.getDbId().equals(e.getDbId()) && StringUtils.equals(psa.getGroupId(), e.getGroupId())
                                && StringUtils.equals(goods.getInternalId(), e.getGoodsInternalId()))
                        .findFirst().orElse(null);
                if (null == storeGoods) {
                    return;
                }
                clone = o2o.clone();
                this.completeO2O(operate, tableName, clone, psa, goods, storeGoods);

            } else if (StringUtils.equals(ElasticEnum.B2C.getChannel(), psa.getChannel())) {
                clone = o2o.clone();
                this.completeO2O(operate, tableName, clone, psa, goods, null);
            }

            if (Objects.nonNull(clone)) {
                collector.collect(new BasicModel<>(tableName, operate, clone, modFieldList));
            }
        });
    }

    /**
     * partner_store_goods ??? es ??????
     * @param tableName
     * @param operate
     * @param goods
     * @param list
     * @param storeGoods
     * @param collector
     */
    private void transferFromStoreGoods2Collect(String tableName, String operate, List<String> modFieldList,
                                                PartnerGoods goods,
                                                List<PartnerStoresAll> list,
                                                PartnerStoreGoods storeGoods,
                                                Collector<BasicModel<ElasticO2O>> collector) {
        List<PartnerStoresAll> collect = list.stream()
                .filter(psa -> StringUtils.equals(ElasticEnum.O2O.getChannel(), psa.getChannel())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            return;
        }
        ElasticO2O o2o = this.transferO2OByGoods(operate,  tableName, goods);
        if (Objects.isNull(o2o)) {
            return;
        }
        collect.forEach(psa -> {
            ElasticO2O clone = o2o.clone();
            this.completeO2O(operate, tableName, clone, psa, goods, storeGoods);
            collector.collect(new BasicModel<>(tableName, operate, clone, modFieldList));
        });
    }


    /**
     * ??????????????????????????????????????????es
     * ??????????????????????????????kafka
     * @param tableName
     * @param operate
     * @param goods
     * @param storeList
     * @param collector
     */
    private void deleteByGoods(String tableName, String operate, PartnerGoods goods, List<PartnerStoresAll> storeList,
                               Collector<BasicModel<ElasticO2O>> collector) {
        storeList.forEach(item -> {
            ElasticO2O o2o = new ElasticO2O();
            o2o.setSkuCode(Compute.skuCode(item, goods));
            o2o.setMerchantId(item.getMerchantId());
            o2o.setStoreId(item.getStoreId());
            o2o.setChannel(item.getChannel());
            o2o.setGoodsInternalId(goods.getInternalId());
            PgcStoreInfoIncrement storeInfo = redisService.queryPgcStoreInfoIncrement(item.getMerchantId(), item.getStoreId());
            o2o.setLocation(Compute.location(storeInfo));
            o2o.setRealTradeCode(goods.getTradeCode());
            collector.collect(new BasicModel<>(tableName, operate, o2o, null));
        });
    }

}
