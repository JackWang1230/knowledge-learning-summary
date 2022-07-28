package cn.wr.collect.sync.dao.gc;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.gc.GoodsDosage;
import cn.wr.collect.sync.utils.MysqlUtils;
import cn.wr.collect.sync.utils.ResultSetConvert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.wr.collect.sync.constants.CommonConstants.SCHEMA_GOODS_CENTER_TIDB;

public class GoodsDosageDao implements QueryLimitDao<GoodsDosage> {
    private static final long serialVersionUID = 8572657859445243856L;
    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsDosageDao.class);

    private static final String SELECT_SQL = "select `id`, `trade_code`, `pack_total`, `pack_total_unit`, `prescription_type`, " +
            " `single_course`, `single_course_unit`, `drug_use`, `standard_dosage_once`, `standard_dosage_once_unit`, " +
            " `standard_dosage_day`, `standard_dosage_day_unit`, `pediatric_use_type`, `pediatric_dosage_once`, " +
            " `pediatric_dosage_once_unit`, `pediatric_dosage_day`, `pediatric_dosage_day_unit`, `pregnancy_use_type`, " +
            " `pregnancy_dosage_once`, `pregnancy_dosage_once_unit`, `pregnancy_dosage_day`, `pregnancy_dosage_day_unit`, " +
            " `geriatric_use_type`, `geriatric_dosage_once`, `geriatric_dosage_once_unit`, `geriatric_dosage_day`, " +
            " `geriatric_dosage_day_unit`, `gmt_created`, `gmt_updated`" +
            " from " + SCHEMA_GOODS_CENTER_TIDB + ".`gc_goods_dosage` " +
            " where id > ? " +
            " order by id asc limit ?;";

    @Override
    public List<GoodsDosage> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        // LOGGER.info("BaseGoods findLimit offset:{}, pageSize:{}", offset, pageSize);
        // long start = System.currentTimeMillis();

        //创建连接
        /*Connection connection = MysqlUtils.getConnection(parameterTool.get(MYSQL_DATABASE_GOODSCENTER_URL),
                parameterTool.get(MYSQL_DATABASE_GOODSCENTER_USERNAME),
                parameterTool.get(MYSQL_DATABASE_GOODSCENTER_PASSWORD));*/

        if (null == connection) return Collections.emptyList();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(SELECT_SQL);
            // ps.setLong(1, offset);
            ps.setLong(1, (long) params.get("id"));
            ps.setInt(2, pageSize);
            rs = ps.executeQuery();
            List<GoodsDosage> list = new ArrayList<>();
            while (rs.next()) {
                GoodsDosage item = new GoodsDosage();
//                "select `id`, `trade_code`, `pack_total`, `pack_total_unit`, `prescription_type`, " +
                item.setId(ResultSetConvert.getLong(rs, 1));
                item.setTradeCode(ResultSetConvert.getString(rs, 2));
                item.setPackTotal(ResultSetConvert.getDouble(rs, 3));
                item.setPackTotalUnit(ResultSetConvert.getInt(rs, 4));
                item.setPrescriptionType(ResultSetConvert.getInt(rs, 5));
//                " `single_course`, `single_course_unit`, `drug_use`, `standard_dosage_once`, `standard_dosage_once_unit`, " +
                item.setSingleCourse(ResultSetConvert.getDouble(rs, 6));
                item.setSingleCourseUnit(ResultSetConvert.getInt(rs, 7));
                item.setDrugUse(ResultSetConvert.getString(rs, 8));
                item.setStandardDosageOnce(ResultSetConvert.getDouble(rs, 9));
                item.setStandardDosageOnceUnit(ResultSetConvert.getInt(rs, 10));
//                " `standard_dosage_day`, `standard_dosage_day_unit`, `pediatric_use_type`, `pediatric_dosage_once`, " +
                item.setStandardDosageDay(ResultSetConvert.getDouble(rs, 11));
                item.setStandardDosageDayUnit(ResultSetConvert.getInt(rs, 12));
                item.setPediatricUseType(ResultSetConvert.getInt(rs, 13));
                item.setPediatricDosageOnce(ResultSetConvert.getDouble(rs, 14));
//                " `pediatric_dosage_once_unit`, `pediatric_dosage_day`, `pediatric_dosage_day_unit`, `pregnancy_use_type`, " +
                item.setPediatricDosageOnceUnit(ResultSetConvert.getInt(rs, 15));
                item.setPediatricDosageDay(ResultSetConvert.getDouble(rs, 16));
                item.setPediatricDosageDayUnit(ResultSetConvert.getInt(rs, 17));
                item.setPregnancyUseType(ResultSetConvert.getInt(rs, 18));
//                " `pregnancy_dosage_once`, `pregnancy_dosage_once_unit`, `pregnancy_dosage_day`, `pregnancy_dosage_day_unit`, " +
                item.setPregnancyDosageOnce(ResultSetConvert.getDouble(rs, 19));
                item.setPregnancyDosageOnceUnit(ResultSetConvert.getInt(rs, 20));
                item.setPregnancyDosageDay(ResultSetConvert.getDouble(rs, 21));
                item.setPregnancyDosageDayUnit(ResultSetConvert.getInt(rs, 22));
//                " `geriatric_use_type`, `geriatric_dosage_once`, `geriatric_dosage_once_unit`, `geriatric_dosage_day`, " +
                item.setGeriatricUseType(ResultSetConvert.getInt(rs, 23));
                item.setGeriatricDosageOnce(ResultSetConvert.getDouble(rs, 24));
                item.setGeriatricDosageOnceUnit(ResultSetConvert.getInt(rs, 25));
                item.setGeriatricDosageDay(ResultSetConvert.getDouble(rs, 26));
//                " `geriatric_dosage_day_unit`, `gmt_created`, `gmt_updated`"
                item.setGeriatricDosageDayUnit(ResultSetConvert.getInt(rs, 27));
                item.setGmtCreated(null != rs.getTimestamp(28) ? rs.getTimestamp(28).toLocalDateTime() : null);
                item.setGmtUpdated(null != rs.getTimestamp(29) ? rs.getTimestamp(29).toLocalDateTime() : null);

                list.add(item);
            }
            return list;
        } catch (Exception e) {
            LOGGER.error("GoodsDosageDao findLimit pageSize:{}, params:{} error:{}", pageSize, params, e);
        } finally {
            MysqlUtils.close(null, ps, rs);
        }
        return Collections.emptyList();
    }
}
