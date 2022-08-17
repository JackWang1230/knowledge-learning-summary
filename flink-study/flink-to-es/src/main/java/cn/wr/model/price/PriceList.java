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
public class PriceList implements Serializable {
    private static final long serialVersionUID = -6797958741708488539L;

    private Long id;        //主键

    @JsonProperty("tenant_id")
    private String tenantId;    //租户id

    @JsonProperty("organization_id")
    private Long organizationId;   //组织id

    @JsonProperty("list_id")
    private String listId;   //清单id

    @JsonProperty("list_name")
    private String listName;      //清单名称

    @JsonProperty("parent_list_id")
    private String parentListId;     //父级id

    @JsonProperty("pids")
    private String pids;     //所有上级

    @JsonProperty("list_code")
    private String listCode;     //清单编号

    @JsonProperty("list_key")
    private String listKey;     //清单key

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
    public PriceList convert(ResultSet rs) throws SQLException {
        this.id = ResultSetConvert.getLong(rs, 1);
        this.tenantId = ResultSetConvert.getString(rs, 2);
        this.organizationId = ResultSetConvert.getLong(rs, 3);
        this.listId = ResultSetConvert.getString(rs, 4);
        this.listName = ResultSetConvert.getString(rs, 5);
        this.parentListId = ResultSetConvert.getString(rs, 6);
        this.pids = ResultSetConvert.getString(rs, 7);
        this.listCode = ResultSetConvert.getString(rs, 8);
        this.listKey = ResultSetConvert.getString(rs, 9);
        this.gmtUpdated = ResultSetConvert.getLocalDateTime(rs, 10);
        this.gmtCreated = ResultSetConvert.getLocalDateTime(rs, 11);
        return this;
    }
}
