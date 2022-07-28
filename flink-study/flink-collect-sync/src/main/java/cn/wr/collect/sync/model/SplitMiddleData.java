package cn.wr.collect.sync.model;

import cn.wr.collect.sync.constants.ElasticEnum;
import cn.wr.collect.sync.model.gc.BaseGoods;
import cn.wr.collect.sync.model.gc.PartnerStoresAll;
import cn.wr.collect.sync.model.partner.PartnerGoods;
import cn.wr.collect.sync.model.partner.PartnerStoreGoods;
import cn.wr.collect.sync.utils.GenerateGoodsNoUtil;
import cn.wr.collect.sync.utils.ParamsValidUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SplitMiddleData implements Serializable {
    private static final long serialVersionUID = 3996377043967441937L;

    private Integer id;
    private String skuCode;
    private String goodsNo;
    private LocalDateTime syncDate;
    private String approvalNumber;
    private BigDecimal salePrice;
    private BigDecimal basePrice;
    private String internalId;
    private String state;
    private String tradeCode;
    private String commonName;
    private String form;
    private Integer goodsSubType;
    private String channel;
    private LocalDateTime gmtCreated;
    private LocalDateTime gmtUpdated;
    private String category;
    private String isOffShelf;
    private String isOffShelfSsg;
    private Integer merchantId;
    private Integer storeId;
    private String geohash;
    private String location;
    private String manufacturer;
    private String groupId;
    private Integer dbId;

    private String price;
    private Integer goodsType;
    private String isStandard;
    private Integer onOff;
    private String sort;
    private LocalDateTime lastDate;
    private LocalDateTime lastPgDate;
    private Integer baiduOnline;

    private String realCommonName;
    private String pack;

    public SplitMiddleData() {
    }

    public SplitMiddleData(PartnerStoresAll psa, PartnerGoods goods, PartnerStoreGoods storeGoods) {
        this.syncDate = LocalDateTime.now();
        this.commonName = null == goods ? null : goods.getCommonName();
        this.realCommonName = null == goods ? null : goods.getCommonName();
        this.basePrice = null == goods ? null : goods.getPrice();
        this.tradeCode = null == goods ? null : goods.getTradeCode();
        this.approvalNumber = null == goods ? null : goods.getApprovalNumber();
        this.form = null == goods ? null : goods.getForm();
        this.channel = null == psa ? null : psa.getChannel();
        this.merchantId = null == psa ? null : psa.getMerchantId();
        this.storeId = null == psa ? null : psa.getStoreId();
        // 经纬度特殊处理  存在脏数据特殊情况处理
        if (null != psa && StringUtils.isNotBlank(psa.getLocation()) && psa.getLocation().contains(",")) {
            String[] split = psa.getLocation().split(",");
            if (split.length == 2 && ParamsValidUtils.gpsValid(split[1], split[0])) {
                this.location = psa.getLocation();
            }
        }
        this.internalId = null == goods ? null : goods.getInternalId();
        this.geohash = null == psa ? null : psa.getHashSeven();
        this.manufacturer = null == goods ? null : goods.getManufacturer();
        this.goodsType = 1;
        this.goodsSubType = 19;
        this.groupId = null == psa ? null : psa.getGroupId();
        this.dbId = null == psa ? null : psa.getDbId();
        if (null != psa && StringUtils.equals(ElasticEnum.B2C.getChannel(), psa.getChannel())) {
            this.salePrice = null == goods ? null : goods.getPrice();
            this.isOffShelfSsg = null != goods && "1".equals(goods.getStatus().toString()) ? "0" : "1";
        }
        else if (null != psa && StringUtils.equals(ElasticEnum.O2O.getChannel(), psa.getChannel())) {
            // isOffShelf 判断取值
            this.isOffShelfSsg = null != goods && "1".equals(goods.getStatus().toString())
                    && null != storeGoods && "1".equals(storeGoods.getStatus().toString()) ? "0" : "1";
            this.salePrice = null != storeGoods ? storeGoods.getPrice() : null;
        }
        this.isOffShelf = this.isOffShelfSsg;
        this.baiduOnline = null == psa ? null : psa.getBaiduOnline();
        this.skuCode = concatSkuCode();

        this.pack = null == goods ? null : goods.getPack();
    }

    /**
     * 获取sku_code
     *
     * @return
     */
    private String concatSkuCode() {
        return this.merchantId + "-" + this.storeId + "-" + this.channel + "-" + this.internalId;
    }
}
