package cn.wr.collect.sync.dao.gc;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.gc.BaseSkuGoods;
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

public class BaseSkuGoodsDao implements QueryLimitDao<BaseSkuGoods> {
    private static final long serialVersionUID = 1912181167218118017L;
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseSkuGoodsDao.class);

    /*private static final String SELECT_SQL = "select `id`, `sku_code`, `bar_code`, `approval_number`, `name`, `en_name`, " +
            "`unspsc`, `brand`, `form`, `width`, `height`, `depth`, `origin_country`, `origin_place`, `assembly_country`, " +
            "`bar_code_type`, `catena`, `is_basic_unit`, `package_type`, `gross_weight`, `net_weight`, `net_content`," +
            " `description`, `keyword`, `price`, `license_num`, `health_permit_num`, `pic`, `ug_category`, `is_check`, " +
            "`source`, `manufacturer_code`, `gmt_updated`, `gmt_created`, `product_manufacturer`, `fetch_success` " +
            " from " + SCHEMA_GOODS_CENTER_TIDB + ".`gc_base_sku_goods` " +
            " where `id` >= (select `id` from " + SCHEMA_GOODS_CENTER_TIDB + ".`gc_base_sku_goods` order by `id` asc limit ?, 1) " +
            " order by `id` asc limit ?;";*/

    private static final String SELECT_SQL = "select `id`, `sku_code`, `bar_code`, `approval_number`, `name`, `en_name`, " +
            "`unspsc`, `brand`, `form`, `width`, `height`, `depth`, `origin_country`, `origin_place`, `assembly_country`, " +
            "`bar_code_type`, `catena`, `is_basic_unit`, `package_type`, `gross_weight`, `net_weight`, `net_content`," +
            " `description`, `keyword`, `price`, `license_num`, `health_permit_num`, `pic`, `ug_category`, `is_check`, " +
            "`source`, `manufacturer_code`, `gmt_updated`, `gmt_created`, `product_manufacturer`, `fetch_success` " +
            " from " + SCHEMA_GOODS_CENTER_TIDB + ".`gc_base_sku_goods` " +
            " where `id` > ? " +
            " order by `id` asc limit ?;";



    @Override
    public List<BaseSkuGoods> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        // LOGGER.info("BaseSkuGoodsDao findLimit offset:{}, pageSize:{}", offset, pageSize);
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
            List<BaseSkuGoods> list = new ArrayList<>(pageSize);
            while (rs.next()) {
                // `id`, `sku_code`, `bar_code`, `approval_number`, `name`, `en_name`, `unspsc`, `brand`, `form`, `width`,
                // `height`, `depth`, `origin_country`, `origin_place`, `assembly_country`, `bar_code_type`, `catena`,
                // `is_basic_unit`, `package_type`, `gross_weight`, `net_weight`, `net_content`, `description`,
                BaseSkuGoods item = new BaseSkuGoods();
                item.setId(rs.getLong(1));
                item.setSkuCode(rs.getString(2));
                item.setBarCode(rs.getString(3));
                item.setApprovalNumber(rs.getString(4));
                item.setName(rs.getString(5));
                item.setEnName(rs.getString(6));
                item.setUnspsc(rs.getString(7));
                item.setBrand(rs.getString(8));
                item.setForm(rs.getString(9));
                item.setWidth(rs.getString(10));
                item.setHeight(rs.getString(11));
                item.setDepth(rs.getString(12));
                item.setOriginCountry(rs.getString(13));
                item.setOriginPlace(rs.getString(14));
                item.setAssemblyCountry(rs.getString(15));
                item.setBarCodeType(rs.getString(16));
                item.setCatena(rs.getString(17));
                item.setIsBasicUnit(rs.getString(18));
                item.setPackageType(rs.getString(19));
                item.setGrossWeight(rs.getString(20));
                item.setNetWeight(rs.getString(21));
                item.setNetContent(rs.getString(22));
                item.setDescription(rs.getString(23));
                // `keyword`, `price`, `license_num`, `health_permit_num`, `pic`, `ug_category`, `is_check`, `source`,
                // `manufacturer_code`, `gmt_updated`, `gmt_created`, `product_manufacturer`, `fetch_success`
                item.setKeyword(rs.getString(24));
                item.setPrice(rs.getString(25));
                item.setLicenseNum(rs.getString(26));
                item.setHealthPermitNum(rs.getString(27));
                item.setPic(rs.getString(28));
                item.setUgCategory(rs.getString(29));
                item.setIsCheck(rs.getInt(30));
                item.setSource(rs.getInt(31));
                item.setManufacturerCode(rs.getString(32));
                item.setGmtUpdated(rs.getTimestamp(33).toLocalDateTime());
                item.setGmtCreated(rs.getTimestamp(34).toLocalDateTime());
                item.setProductManufacturer(rs.getString(35));
                item.setFetchSuccess(rs.getInt(36));
                list.add(item);
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("BaseSkuGoodsDao findLimit offset:{}, pageSize:{}, error:{}", offset, pageSize, e);
        }
        finally {
            // long end = System.currentTimeMillis();
            // LOGGER.error("BaseSkuGoodsDao findLimit offset:{}, pageSize:{}, time:{}(s)", offset, pageSize, (end - start)/1000);
            MysqlUtils.close(null, ps, rs);
        }
        return Collections.emptyList();
    }
}
