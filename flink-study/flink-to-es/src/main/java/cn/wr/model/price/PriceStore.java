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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * @author RWang
 * @Date 2022/8/16
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceStore implements Serializable {
    private static final long serialVersionUID = -7858471442336315535L;

    @JsonProperty("id")
    private Long id;        //主键

    @JsonProperty("tenant_id")
    private Long tenantId;    //租户id

    @JsonProperty("organization_id")
    private Long organizationId;   //组织id

    @JsonProperty("store_id")
    private Long storeId;       //门店id

    @JsonProperty("list_id")
    private String listId;      //清单id

    @JsonProperty("ud_list_id")
    private String udListId;     //药联清单id

    @JsonProperty("store_key")
    private String storeKey;     //门店key

    @JsonProperty("gmt_updated")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using =
            LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime gmtUpdated;

    @JsonProperty("gmt_created")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using =
            LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
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
