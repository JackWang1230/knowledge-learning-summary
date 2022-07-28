package cn.wr.collect.sync.model.price;

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
@Table(name = "price_list")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceList implements Model {

    private static final long serialVersionUID = 7626466628511263922L;

    @Column(name = "id")
    private Long id;        //主键

    @Column(name = "tenant_id")
    @Correspond(type = Correspond.Type.Key)
    private String tenantId;    //租户id

    @QueryField(order = 0)
    @Column(name = "organization_id")
    @Correspond(type = Correspond.Type.Key)
    private Long organizationId;   //组织id

    @QueryField(order = 1)
    @Column(name = "list_id")
    @Correspond(type = Correspond.Type.Key)
    private String listId;   //清单id

    @Column(name = "list_name")
    private String listName;      //清单名称

    @Column(name = "parent_list_id")
    @Correspond(type = Correspond.Type.Key)
    private String parentListId;     //父级id

    @Column(name = "pids")
    private String pids;     //所有上级

    @Column(name = "list_code")
    private String listCode;     //清单编号

    @Column(name = "list_key")
    private String listKey;     //清单key

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
