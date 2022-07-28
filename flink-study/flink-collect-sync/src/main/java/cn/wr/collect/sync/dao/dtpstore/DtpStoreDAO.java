package cn.wr.collect.sync.dao.dtpstore;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.model.DtpStore;
import cn.wr.collect.sync.utils.MysqlUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;


public class DtpStoreDAO {
    private static final Logger log = LoggerFactory.getLogger(DtpStoreDAO.class);
    private final ParameterTool parameterTool;
    // dbname 正常情况下为：partner_common_0， 存在异常值：health_common_01/partner_common_data/partner_common_test
    // sql截取字符串partner_common_，正则校验db_id
    private static final String SELECT_SQL = "select " +
            " (case when (substr(p.dbname, 16) REGEXP '[^0-9.]') = 0 then substr(p.dbname, 16) else null end) as db_id," +
            " ob.organizationId as store_id " +
            " from " + CommonConstants.SCHEMA_UNION_DRUG_PARTNER + ".partners p " +
            " inner join " + CommonConstants.SCHEMA_GOODS_CENTER_TIDB + ".organize_base ob on p.organizationId = ob.rootId " +
            " where ob.netType = 2 ;";

    private static final String SELECT_SQL_02 = "select distinct organizationId " +
            " from " + CommonConstants.SCHEMA_UNION_DRUG_PARTNER + ".partners" +
            " where dbname = concat('partner_common_', ?)";

    public DtpStoreDAO(ParameterTool parameterTool) {
        this.parameterTool = parameterTool;
    }

    /**
     * 根据门店id查询门店详情
     * @param params
     * @return
     */
    public List<DtpStore> queryList(Map<String, Object> params) {
        int i = 0;
        while (i < 100) {
            try {
                return query(params);
            }
            catch (Exception e) {
                log.error("DtpStoreDAO queryList Exception:{}", e);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    log.error("DtpStoreDAO queryById InterruptedException:{}", e1);
                }
            }
            i ++;
        }
        return null;
    }

    /**
     * 根据条件查询门店详情
     * @param params
     * @return
     * @throws Exception
     */
    private List<DtpStore> query(Map<String, Object> params) throws Exception {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_GOODSCENTER_URL),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_PASSWORD));
            ps = connection.prepareStatement(SELECT_SQL);
            rs = ps.executeQuery();
            List<DtpStore> dtpStoreList = new ArrayList<>();
            while (rs.next()) {
                dtpStoreList.add(new DtpStore().convert(rs));
            }
            return dtpStoreList;
        }
        catch (Exception e) {
            log.error("DtpStoreDAO params:{} error:{}", params, e);
            throw new Exception(e);
        }
        finally {
            log.info("DtpStoreDAO params:{}", params);
            MysqlUtils.close(connection, ps, rs);
        }
    }

    /**
     * 根据条件查询门店详情
     * @param dbId
     * @return
     * @throws Exception
     */
    public List<Integer> queryMerchantIdListByDbId(Integer dbId) throws Exception {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_GOODSCENTER_URL),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_PASSWORD));
            ps = connection.prepareStatement(SELECT_SQL_02);
            ps.setInt(1, dbId);
            rs = ps.executeQuery();
            List<Integer> dtpStoreList = new ArrayList<>();
            while (rs.next()) {
                dtpStoreList.add(rs.getInt(1));
            }
            return dtpStoreList;
        }
        catch (Exception e) {
            log.error("DtpStoreDAO dbId:{} error:{}", dbId, e);
            throw new Exception(e);
        }
        finally {
            log.info("DtpStoreDAO dbId:{}", dbId);
            MysqlUtils.close(connection, ps, rs);
        }
    }
}
