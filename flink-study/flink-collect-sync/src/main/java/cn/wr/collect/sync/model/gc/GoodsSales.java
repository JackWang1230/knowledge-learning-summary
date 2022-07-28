package cn.wr.collect.sync.model.gc;

import cn.wr.collect.sync.model.annotations.Column;
import cn.wr.collect.sync.model.annotations.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "goods_sales")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoodsSales {

    /**
     * 销量
     */
    @Column(name = "quantity")
    private Double quantity;
}
