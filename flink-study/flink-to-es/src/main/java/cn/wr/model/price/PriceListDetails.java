package cn.wr.model.price;

import cn.wr.utils.ResultSetConvert;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * @author RWang
 * @Date 2022/8/16
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceListDetails implements Serializable {
    private static final long serialVersionUID = 2528283604628918489L;

    @JsonProperty("id")
    private Long id;        //主键

    @JsonProperty("tenant_id")
    private String tenantId;    //租户id

    @JsonProperty("organization_id")
    private Long organizationId;    //组织id

    @JsonProperty("list_id")
    private String listId;    //清单id

    @JsonProperty("internal_id")
    private String internalId;  //商品内码

    @JsonProperty("sku_price")
    private BigDecimal skuPrice;  //售价

    @JsonProperty("original_price")
    private BigDecimal originalPrice;  //原价

    @JsonProperty("member_price")
    private BigDecimal memberPrice;  //会员价

    @JsonProperty("min_price")
    private BigDecimal minPrice;  //自定义最小价格

    @JsonProperty("max_price")
    private BigDecimal maxPrice;  //自定义最大价格

    @JsonProperty("channel")
    private Integer channel;     //渠道

    @JsonProperty("sub_channel")
    private Integer subChannel;   //子渠道

    @JsonProperty("list_status")
    private Integer listStatus; //清单状态

    @JsonProperty("source")
    private Integer source;  //来源

    @JsonProperty("detail_key")
    private String detailKey;  //详情

    @JsonProperty("gmt_updated")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using =
            LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime gmtUpdated;


    @JsonProperty("gmt_created")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
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
