package cn.wr.collect.sync.model.gc;


import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Column;
import cn.wr.collect.sync.model.annotations.QueryField;
import cn.wr.collect.sync.model.annotations.Table;
import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Table(name = "parnter_goods_search_priority")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParnterGoodsSearchPriority implements Model {

    private static final long serialVersionUID = -6897138508819021997L;
    @Column(name = "id")
    private Long id;
    @QueryField(order = 0)
    @Column(name = "db_id")
    private Integer dbId;
    @QueryField(order = 1)
    @Column(name = "merchant_id")
    private Integer merchantId;
    @QueryField(order = 2)
    @Column(name = "internal_id")
    private String internalId;
    @Column(name = "priority")
    private Integer priority;

}
