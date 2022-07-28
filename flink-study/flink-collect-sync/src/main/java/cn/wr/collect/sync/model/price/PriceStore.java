package cn.wr.collect.sync.model.price;

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
@Table(name = "price_store")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceStore implements Model {
    private static final long serialVersionUID = 7626466628547163922L;

    @Column(name = "id")
    private Long id;        //主键

    @Column(name = "tenant_id")
    @Correspond(field = {EsFieldConst.sale_price})
    private Long tenantId;    //租户id

    @QueryField(order = 0)
    @Column(name = "organization_id")
    @Correspond(type = Correspond.Type.Key)
    private Long organizationId;   //组织id

    @QueryField(order = 1)
    @Column(name = "store_id")
    @Correspond(type = Correspond.Type.Key)
    private Long storeId;       //门店id

    @Column(name = "list_id")
    @Correspond(type = Correspond.Type.Key)
    private String listId;      //清单id

    @Column(name = "ud_list_id")
    private String udListId;     //药联清单id

    @Column(name = "store_key")
    private String storeKey;     //门店key

    @Column(name = "gmt_updated")
    private LocalDateTime gmtUpdated;

    @Column(name = "gmt_created")
    private LocalDateTime gmtCreated;

    /**
     * 参数转换
     * @param rs
     * @return
     * @throws SQLException
     */
    public PriceStore convert(ResultSet rs) throws SQLException {
        this.id = ResultSetConvert.getLong(rs, 1);
        this.tenantId = ResultSetConvert.getLong(rs, 2);
        this.organizationId = ResultSetConvert.getLong(rs, 3);
        this.storeId = ResultSetConvert.getLong(rs, 4);
        this.listId = ResultSetConvert.getString(rs, 5);
        this.udListId = ResultSetConvert.getString(rs, 6);
        this.storeKey = ResultSetConvert.getString(rs, 7);
        this.gmtUpdated = ResultSetConvert.getLocalDateTime(rs, 8);
        this.gmtCreated = ResultSetConvert.getLocalDateTime(rs, 9);
        return this;
    }
}
