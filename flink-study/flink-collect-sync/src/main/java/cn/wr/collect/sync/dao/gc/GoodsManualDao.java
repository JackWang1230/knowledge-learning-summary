package cn.wr.collect.sync.dao.gc;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.gc.GoodsManual;
import cn.wr.collect.sync.utils.MysqlUtils;
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
import static cn.wr.collect.sync.constants.PropertiesConstants.*;

public class GoodsManualDao implements QueryLimitDao<GoodsManual> {
    private static final long serialVersionUID = -4330873176653769921L;
    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsManualDao.class);


    private static final String SELECT_SQL = "select `id`, `common_name`, `en_name`, `pinyin_name`, `approval_number`, " +
            " `taboo`, `interaction`, `composition`, `pharmacological_effects`, `dosage`, `clinical_classification`, " +
            " `cure_disease`, `attentions`, `manufacturer`, `specification`, `pharmacokinetics`, `storage`, `pediatric_use`, " +
            " `geriatric_use`, `pregnancy_and_nursing_mothers`, `over_dosage`, `validity`, `drug_name`, `relative_sickness`, " +
            " `prescription_type`, `indications`, `drug_type`, `packaging`, `gmtCreated`, `gmtUpdated` " +
            " from " + SCHEMA_GOODS_CENTER_TIDB + ".`gc_goods_manual` " +
            " where `id` > ? " +
            " order by `id` asc limit ?;";


    @Override
    public List<GoodsManual> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        // LOGGER.info("GoodsManualDao findLimit offset:{}, pageSize:{}", offset, pageSize);
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
            List<GoodsManual> list = new ArrayList<>(pageSize);
            while (rs.next()) {
                // `id`, `common_name`, `en_name`, `pinyin_name`, `approval_number`, `taboo`, `interaction`,
                // `composition`, `pharmacological_effects`, `dosage`, `clinical_classification`, `cure_disease`,
                GoodsManual item = new GoodsManual();
                item.setId(rs.getLong(1));
                item.setCommonName(rs.getString(2));
                item.setEnName(rs.getString(3));
                item.setPinyinName(rs.getString(4));
                item.setApprovalNumber(rs.getString(5));
                item.setTaboo(rs.getString(6));
                item.setInteraction(rs.getString(7));
                item.setComposition(rs.getString(8));
                item.setPharmacologicalEffects(rs.getString(9));
                item.setDosage(rs.getString(10));
                item.setClinicalClassification(rs.getString(11));
                item.setCureDisease(rs.getString(12));
                // `attentions`, `manufacturer`, `specification`, `pharmacokinetics`, `storage`, `pediatric_use`,
                // `geriatric_use`, `pregnancy_and_nursing_mothers`, `over_dosage`, `validity`, `drug_name`,
                item.setAttentions(rs.getString(13));
                item.setManufacturer(rs.getString(14));
                item.setSpecification(rs.getString(15));
                item.setPharmacokinetics(rs.getString(16));
                item.setStorage(rs.getString(17));
                item.setPediatricUse(rs.getString(18));
                item.setGeriatricUse(rs.getString(19));
                item.setPregnancyAndNursingMothers(rs.getString(20));
                item.setOverDosage(rs.getString(21));
                item.setValidity(rs.getString(22));
                item.setDrugName(rs.getString(23));
                // `relative_sickness`, `prescription_type`, `indications`, `drug_type`, `packaging`, `gmtCreated`,
                // `gmtUpdated`
                item.setRelativeSickness(rs.getString(24));
                item.setPrescriptionType(rs.getInt(25));
                item.setIndications(rs.getString(26));
                item.setDrugType(rs.getString(27));
                item.setPackaging(rs.getString(28));
                item.setGmtCreated(null != rs.getTimestamp(29) ? rs.getTimestamp(29).toLocalDateTime() : null);
                item.setGmtUpdated(null != rs.getTimestamp(30) ? rs.getTimestamp(30).toLocalDateTime() : null);
                list.add(item);
            }
            return list;
        } catch (Exception e) {
            LOGGER.error("GoodsManualDao findLimit offset:{}, pageSize:{}, error:{}", offset, pageSize, e);
        }
        finally {
            // long end = System.currentTimeMillis();
            // LOGGER.error("GoodsManualDao findLimit offset:{}, pageSize:{}, time:{}(s)", offset, pageSize, (end - start)/1000);
            MysqlUtils.close(null, ps, rs);
        }
        return Collections.emptyList();
    }

}
