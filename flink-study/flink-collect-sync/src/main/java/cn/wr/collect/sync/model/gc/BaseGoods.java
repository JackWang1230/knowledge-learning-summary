package cn.wr.collect.sync.model.gc;



import cn.wr.collect.sync.constants.EsFieldConst;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Column;
import cn.wr.collect.sync.model.annotations.Correspond;
import cn.wr.collect.sync.model.annotations.QueryField;
import cn.wr.collect.sync.model.annotations.Table;
import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Table(name = "base_goods")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseGoods implements Model {

  private static final long serialVersionUID = -7266979659773563135L;
  @Column(name = "id")
  private Long id;
  @QueryField
  @Correspond(type = Correspond.Type.Key, field = EsFieldConst.approval_number)
  @Column(name = "approval_number")
  private String approvalNumber;
  @Column(name = "product_name")
  @Correspond(field = {EsFieldConst.common_name}, mode = Correspond.Mode.Multi)
  private String productName;
  @Column(name = "spec")
  private String spec;
  @Column(name = "product_unit")
  private String productUnit;
  @Column(name = "product_addr")
  private String productAddr;
  @Column(name = "approve_date")
  private String approveDate;
  @Column(name = "end_date")
  private String endDate;
  @Column(name = "goods_type")
  private Integer goodsType;
  @Column(name = "domestic_foreign")
  private Integer domesticForeign;
  @Column(name = "relation_id")
  private Long relationId;
  @Column(name = "cate_one")
  private String cateOne;
  @Column(name = "cate_two")
  private String cateTwo;
  @Column(name = "cate_three")
  private String cateThree;
  @Column(name = "cate_four")
  private String cateFour;

  @Column(name = "cate_five")
  /*@Correspond(field = {EsFieldConst.category_one, EsFieldConst.category_two, EsFieldConst.category_three,
          EsFieldConst.category_four, EsFieldConst.category_five})*/
  private String cateFive;

}
