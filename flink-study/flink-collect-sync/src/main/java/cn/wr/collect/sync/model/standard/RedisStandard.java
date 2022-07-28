package cn.wr.collect.sync.model.standard;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RedisStandard implements Serializable {
  private static final long serialVersionUID = 6536412286108162830L;
  private String id;
  private Long spuId;

  private String goodsName;
  private String mainTitle;
  private String subTitle;
  private String tradeCode;
  private String specification;
  private BigDecimal retailPrice;
  private String grossWeight;
  private String originPlace;
  private String brand;
  private Long categoryId;
  private String searchKeywords;
  private String searchAssociationWords;
  private String highlights;
  private String detailsCode;
  private String urls;
  private Integer status;
  private String approvalNumber;
  private String attrPtitle;
  private String attrCtitle;
  private BigDecimal weight;
  private BigDecimal volume;
  private String joinRemarks;
  /**
   * 拼音简拼
   */
  private String simplePinyin;
  /**
   * 拼音全拼
   */
  private String fullPinyin;

  /** 属性标签 */
  private List<RedisAttrs> redisAttrs;
  /** 分类 */
  private List<RedisCates> redisCates;

  /**
   * 平台展示价
   */
  private BigDecimal middlePrice;
  /**
   * 总销量
   */
  private BigDecimal historySales;
  /**
   * 线上销售门店数
   */
  private Integer storeNum;

}
