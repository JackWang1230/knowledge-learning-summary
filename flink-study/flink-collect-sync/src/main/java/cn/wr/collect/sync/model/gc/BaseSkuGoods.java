package cn.wr.collect.sync.model.gc;


import cn.wr.collect.sync.common.CustomLocalDateTimeDeserializer;
import cn.wr.collect.sync.common.CustomLocalDateTimeSerializer;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Column;
import cn.wr.collect.sync.model.annotations.Table;
import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;

@Data
@Table(name = "gc_base_sku_goods")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseSkuGoods implements Model {

  private static final long serialVersionUID = -437392507632349411L;
  @Column(name = "id")
  private Long id;
  @Column(name = "sku_code")
  private String skuCode;
  @Column(name = "bar_code")
  private String barCode;
  @Column(name = "approval_number")
  private String approvalNumber;
  @Column(name = "name")
  private String name;
  @Column(name = "en_name")
  private String enName;
  @Column(name = "unspsc")
  private String unspsc;
  @Column(name = "brand")
  private String brand;
  @Column(name = "form")
  private String form;
  @Column(name = "width")
  private String width;
  @Column(name = "height")
  private String height;
  @Column(name = "depth")
  private String depth;
  @Column(name = "origin_country")
  private String originCountry;
  @Column(name = "origin_place")
  private String originPlace;
  @Column(name = "assembly_country")
  private String assemblyCountry;
  @Column(name = "bar_code_type")
  private String barCodeType;
  @Column(name = "catena")
  private String catena;
  @Column(name = "is_basic_unit")
  private String isBasicUnit;
  @Column(name = "package_type")
  private String packageType;
  @Column(name = "gross_weight")
  private String grossWeight;
  @Column(name = "net_weight")
  private String netWeight;
  @Column(name = "net_content")
  private String netContent;
  @Column(name = "description")
  private String description;
  @Column(name = "keyword")
  private String keyword;
  @Column(name = "price")
  private String price;
  @Column(name = "license_num")
  private String licenseNum;
  @Column(name = "health_permit_num")
  private String healthPermitNum;
  @Column(name = "pic")
  private String pic;
  @Column(name = "ug_category")
  private String ugCategory;
  @Column(name = "is_check")
  private Integer isCheck;
  @Column(name = "source")
  private Integer source;
  @Column(name = "manufacturer_code")
  private String manufacturerCode;
  @Column(name = "gmt_updated")
  @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
  @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
  private LocalDateTime gmtUpdated;
  @Column(name = "gmt_created")
  @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
  @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
  private LocalDateTime gmtCreated;
  @Column(name = "product_manufacturer")
  private String productManufacturer;
  @Column(name = "fetch_success")
  private Integer fetchSuccess;

}
