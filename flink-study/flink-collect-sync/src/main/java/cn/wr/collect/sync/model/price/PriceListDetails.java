package cn.wr.collect.sync.model.price;

import cn.wr.collect.sync.constants.EsFieldConst;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Column;
import cn.wr.collect.sync.model.annotations.Correspond;
import cn.wr.collect.sync.model.annotations.QueryField;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.utils.ResultSetConvert;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Data
@Table(name = "price_list_details")
public class PriceListDetails implements Model {

    private static final long serialVersionUID = 7626413628511263922L;

    @Column(name = "id")
    private Long id;        //主键

    @Column(name = "tenant_id")
    private String tenantId;    //租户id

    @QueryField(order = 0)
    @Correspond(type = Correspond.Type.Key)
    @Column(name = "organization_id")
    private Long organizationId;    //组织id

    @QueryField(order = 1)
    @Correspond(type = Correspond.Type.Key)
    @Column(name = "list_id")
    private String listId;    //清单id

    @QueryField(order = 2)
    @Correspond(type = Correspond.Type.Key)
    @Column(name = "internal_id")
    private String internalId;  //商品内码

    @Column(name = "sku_price")
    @Correspond(field = {EsFieldConst.sale_price})
    private BigDecimal skuPrice;  //售价

    @Column(name = "original_price")
    @Correspond(field = {EsFieldConst.base_price})
    private BigDecimal originalPrice;  //原价

    @Column(name = "member_price")
    private BigDecimal memberPrice;  //会员价

    @Column(name = "min_price")
    private BigDecimal minPrice;  //自定义最小价格

    @Column(name = "max_price")
    private BigDecimal maxPrice;  //自定义最大价格

    @QueryField(order = 3)
    @Column(name = "channel")
    private Integer channel;     //渠道

    @QueryField(order = 4)
    @Column(name = "sub_channel")
    private Integer subChannel;   //子渠道

    @Column(name = "list_status")
    private Integer listStatus; //清单状态

    @Column(name = "source")
    private Integer source;  //来源

    @Column(name = "detail_key")
    private String detailKey;  //详情

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
    public PriceListDetails convert(ResultSet rs) throws SQLException {
        this.id = ResultSetConvert.getLong(rs, 1);
        this.tenantId = ResultSetConvert.getString(rs, 2);
        this.organizationId = ResultSetConvert.getLong(rs, 3);
        this.listId = ResultSetConvert.getString(rs, 4);
        this.internalId = ResultSetConvert.getString(rs, 5);
        this.skuPrice = ResultSetConvert.getBigDecimal(rs, 6);
        this.originalPrice = ResultSetConvert.getBigDecimal(rs, 7);
        this.memberPrice = ResultSetConvert.getBigDecimal(rs, 8);
        this.minPrice = ResultSetConvert.getBigDecimal(rs, 9);
        this.maxPrice = ResultSetConvert.getBigDecimal(rs, 10);
        this.channel = ResultSetConvert.getInt(rs, 11);
        this.subChannel = ResultSetConvert.getInt(rs, 12);
        this.listStatus =  ResultSetConvert.getInt(rs, 13);
        this.source = ResultSetConvert.getInt(rs, 14);
        this.detailKey = ResultSetConvert.getString(rs, 15);
        this.gmtUpdated = ResultSetConvert.getLocalDateTime(rs, 16);
        this.gmtCreated = ResultSetConvert.getLocalDateTime(rs, 17);
        return this;
    }
}
