package cn.wr.collect.sync.dao.partner;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.partner.PartnerGoodsInfo;
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

import static cn.wr.collect.sync.constants.CommonConstants.SCHEMA_UNION_DRUG_PARTNER;
import static cn.wr.collect.sync.constants.PropertiesConstants.*;

public class PartnerGoodsInfoDao implements QueryLimitDao<PartnerGoodsInfo> {
    private static final long serialVersionUID = -4944619361549171009L;
    private static final Logger LOGGER = LoggerFactory.getLogger(PartnerGoodsInfoDao.class);

//
    private static final String SELECT_SQL = "select `id`, `db_id`, `table_id`, `internal_id`, `trade_code`, `approval_number`, " +
            " `price`, `trade_name`, `common_name`, `english_name`, `manufacturer`, `otc`, `medical_insurance`, `storage`, " +
            " `appearance`, `diseases`, `drug_form`, `spec`, `sku`, `package`, `validity_period`, " +
            " (CASE `expiry_date` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `expiry_date` END) AS `expiry_date`, " +
            " `component`, " +
            " `content`, `interaction`, `images`, `indications`, `dosage`, `pharmacological`, `contraindication`, `caution`, " +
            " `adverse_reaction`, `excess_reaction`, `child_effect`, `elder_effect`, `pregnant_effect`, `warning`, `category`, " +
            " (CASE `created_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `created_at` END) AS `created_at`, " +
            " (CASE `updated_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `updated_at` END) AS `updated_at`, " +
            " (CASE `gmtcreated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmtcreated` END) AS `gmtcreated`, " +
            " (CASE `gmtupdated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmtupdated` END) AS `gmtupdated` " +
            " from " + SCHEMA_UNION_DRUG_PARTNER + ".`partner_goods_info` " +
            " where id > ? " +
            " order by id asc limit ?;";

    @Override
    public List<PartnerGoodsInfo> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        // LOGGER.info("PartnerGoodsInfoDao findLimit offset:{}, pageSize:{}", offset, pageSize);
        // long start = System.currentTimeMillis();
        //创建连接
        if (null == connection) return Collections.emptyList();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(SELECT_SQL);
            // ps.setLong(1, offset);
            ps.setLong(1, (long) params.get("id"));
            ps.setInt(2, pageSize);
            rs = ps.executeQuery();
            List<PartnerGoodsInfo> list = new ArrayList<>(pageSize);
            while (rs.next()) {
                // `id`, `db_id`, `table_id`, `internal_id`, `trade_code`, `approval_number`, `price`, `trade_name`,
                // `common_name`, `english_name`, `manufacturer`, `otc`, `medical_insurance`, `storage`,
                PartnerGoodsInfo item = new PartnerGoodsInfo();
                item.setId(rs.getLong(1));
                item.setDbId(rs.getInt(2));
                item.setTableId(rs.getInt(3));
                item.setInternalId(rs.getString(4));
                item.setTradeCode(rs.getLong(5));
                item.setApprovalNumber(rs.getString(6));
                item.setPrice(rs.getBigDecimal(7));
                item.setTradeName(rs.getString(8));
                item.setCommonName(rs.getString(9));
                item.setEnglishName(rs.getString(10));
                item.setManufacturer(rs.getString(11));
                item.setOtc(rs.getString(12));
                item.setMedicalInsurance(rs.getString(13));
                item.setStorage(rs.getString(14));
                // `appearance`, `diseases`, `drug_form`, `spec`, `sku`, `package`, `validity_period`,
                // `expiry_date`, `component`, `content`, `interaction`, `images`, `indications`, `dosage`,
                item.setAppearance(rs.getString(15));
                item.setDiseases(rs.getString(16));
                item.setDrugForm(rs.getString(17));
                item.setSpec(rs.getString(18));
                item.setSku(rs.getString(19));
                item.setPackagee(rs.getString(20));
                item.setValidityPeriod(rs.getString(21));
                item.setExpiryDate(null != rs.getTimestamp(22) ? rs.getTimestamp(22).toLocalDateTime() : null);
                item.setComponent(rs.getString(23));
                item.setContent(rs.getString(24));
                item.setInteraction(rs.getString(25));
                item.setImages(rs.getString(26));
                item.setIndications(rs.getString(27));
                item.setDosage(rs.getString(28));
                // `pharmacological`, `contraindication`, `caution`, `adverse_reaction`, `excess_reaction`,
                // `child_effect`, `elder_effect`, `pregnant_effect`, `warning`, `category`, `created_at`,
                // `updated_at`, `gmtcreated`, `gmtupdated`
                item.setPharmacological(rs.getString(29));
                item.setContraindication(rs.getString(30));
                item.setCaution(rs.getString(31));
                item.setAdverseReaction(rs.getString(32));
                item.setExcessReaction(rs.getString(33));
                item.setChildEffect(rs.getString(34));
                item.setElderEffect(rs.getString(35));
                item.setPregnantEffect(rs.getString(36));
                item.setWarning(rs.getString(37));
                item.setCategory(rs.getString(38));
                item.setCreatedAt(null != rs.getTimestamp(39) ? rs.getTimestamp(39).toLocalDateTime() : null);
                item.setUpdatedAt(null != rs.getTimestamp(40) ? rs.getTimestamp(40).toLocalDateTime() : null);
                item.setGmtcreated(null != rs.getTimestamp(41) ? rs.getTimestamp(41).toLocalDateTime() : null);
                item.setGmtupdated(null != rs.getTimestamp(42) ? rs.getTimestamp(42).toLocalDateTime() : null);
                list.add(item);
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("PartnerGoodsInfoDao findLimit offset:{}, pageSize:{}, error:{}", offset, pageSize, e);
        }
        finally {
            // long end = System.currentTimeMillis();
            // LOGGER.error("PartnerGoodsInfoDao findLimit offset:{}, pageSize:{}, time:{}(s)", offset, pageSize, (end - start)/1000);
            MysqlUtils.close(null, ps, rs);
        }

        return Collections.emptyList();
    }
}
