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
@Table(name = "gc_category_info")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryInfo implements Model {
    private static final long serialVersionUID = 4856407641170046827L;
    @QueryField
    @Column(name = "id")
    @Correspond(type = Correspond.Type.Key)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "key_word")
    private String keyWord;
    /**
     * 父级ID
     */
    @Column(name = "parent_id")
    private Long parentId;
    @Column(name = "pids")
    @Correspond(mode = Correspond.Mode.Multi, field = {EsFieldConst.cate_ids})
    private String pids;
    /**
     * 分类层级
     */
    @Column(name = "level")
    private Integer level;
    @Column(name = "remarks")
    private String remarks;
    /**
     * 分类类型  默认0 后台分类  1前台分类  2低毛分类
     */
    @Column(name = "type")
    private Integer type;
    /**
     * 分类方案ID
     */
    @Column(name = "plan_id")
    private Long planId;
    /**
     * 删除标记 0 正常 1 停用
     */
    @Correspond(mode = Correspond.Mode.Multi, field = {EsFieldConst.cate_ids})
    @Column(name = "deleted")
    private Integer deleted;
    /**
     * 是否修改：0：不允许增加、删除、修改 1：允许
     */
    @Column(name = "allow")
    private Integer allow;
    @Column(name = "creator")
    private String creator;
    @Column(name = "updator")
    private String updator;
    /**
     * 更新时间
     */
    @Column(name = "gmt_updated")
    private LocalDateTime gmtUpdated;
    /**
     * 创建时间
     */
    @Column(name = "gmt_created")
    private LocalDateTime gmtCreated;
    /**
     * 0 手工分类  1 最近上新 2热销排行
     */
    @Column(name = "auto_type")
    private Integer autoType;

    public CategoryInfo convert(ResultSet rs) throws SQLException {
        this.setId(ResultSetConvert.getLong(rs, 1));
        this.setTitle(ResultSetConvert.getString(rs, 2));
        this.setKeyWord(ResultSetConvert.getString(rs, 3));
        this.setParentId(ResultSetConvert.getLong(rs, 4));
        this.setPids(ResultSetConvert.getString(rs, 5));
        this.setLevel(ResultSetConvert.getInt(rs, 6));
        this.setRemarks(ResultSetConvert.getString(rs, 7));
        this.setType(ResultSetConvert.getInt(rs, 8));
        this.setPlanId(ResultSetConvert.getLong(rs, 9));
        this.setDeleted(ResultSetConvert.getInt(rs, 10));
        this.setAllow(ResultSetConvert.getInt(rs, 11));
        this.setCreator(ResultSetConvert.getString(rs, 12));
        this.setUpdator(ResultSetConvert.getString(rs, 13));
        this.setGmtUpdated(ResultSetConvert.getLocalDateTime(rs, 14));
        this.setGmtCreated(ResultSetConvert.getLocalDateTime(rs, 15));
        this.setAutoType(ResultSetConvert.getInt(rs, 16));
        return this;
    }
}
