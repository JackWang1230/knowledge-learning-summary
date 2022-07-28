package cn.wr.collect.sync.model.gc;

import cn.wr.collect.sync.common.CustomLocalDateTimeDeserializer;
import cn.wr.collect.sync.common.CustomLocalDateTimeSerializer;
import cn.wr.collect.sync.constants.EsFieldConst;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Column;
import cn.wr.collect.sync.model.annotations.Correspond;
import cn.wr.collect.sync.model.annotations.QueryField;
import cn.wr.collect.sync.utils.ResultSetConvert;
import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;


@Data
public class GoodsSpu implements Model {
    private static final long serialVersionUID = 8882114470127789214L;

    @QueryField
    @Correspond(type = Correspond.Type.Key)
    @Column(name = "id")
    private Long id;

    @Correspond(field = {EsFieldConst.indications,
            EsFieldConst.drug_name, EsFieldConst.relative_sickness,
            EsFieldConst.common_name, EsFieldConst.is_prescription, EsFieldConst.img,
            EsFieldConst.approval_number}, mode = Correspond.Mode.Multi)
    @Column(name = "approval_number")
    private String approvalNumber;

    @Column(name = "gmtCreated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtCreated;

    @Column(name = "gmtUpdated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtUpdated;

    public GoodsSpu convert(ResultSet rs) throws SQLException {
        this.setId(ResultSetConvert.getLong(rs, 1));
        this.setApprovalNumber(ResultSetConvert.getString(rs, 2));
        this.setGmtCreated(ResultSetConvert.getLocalDateTime(rs, 3));
        this.setGmtUpdated(ResultSetConvert.getLocalDateTime(rs, 4));
        return this;
    }
}
