package cn.wr.collect.sync.model.gc;

import cn.wr.collect.sync.constants.EsFieldConst;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Column;
import cn.wr.collect.sync.model.annotations.Correspond;
import cn.wr.collect.sync.model.annotations.QueryField;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.utils.ResultSetConvert;
import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;


@Data
@Table(name = "gc_goods_cate_spu")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoodsCateSpu implements Model {
    private static final long serialVersionUID = -6095472325602687018L;

    @Column(name = "id")
    private Long id;

    /**
     * 批准文号
     */
    @Column(name = "approval_number")
    private String approvalNumber;

    /**
     * 条码
     */
    @QueryField
    @Column(name = "bar_code")
    @Correspond(type = Correspond.Type.Key)
    private String barCode;

    @QueryField(order = 1)
    @Column(name = "cate_id")
    @Correspond(mode = Correspond.Mode.Multi, field = {EsFieldConst.cate_ids})
    private Long cateId;

    /**
     * 创建时间
     */
    @Column(name = "gmtCreated")
    private LocalDateTime gmtCreated;

    /**
     * 更新时间
     */
    @Column(name = "gmtUpdated")
    private LocalDateTime gmtUpdated;

    /**
     * 操作人
     */
    @Column(name = "operator")
    private String operator;


    public GoodsCateSpu convert(ResultSet rs) throws SQLException {
        this.setId(ResultSetConvert.getLong(rs, 1));
        this.setApprovalNumber(ResultSetConvert.getString(rs, 2));
        this.setBarCode(ResultSetConvert.getString(rs, 3));
        this.setCateId(ResultSetConvert.getLong(rs, 4));
        this.setGmtCreated(ResultSetConvert.getLocalDateTime(rs, 5));
        this.setGmtUpdated(ResultSetConvert.getLocalDateTime(rs, 6));
        this.setOperator(ResultSetConvert.getString(rs, 7));
        return this;
    }
}
