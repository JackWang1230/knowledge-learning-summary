package cn.wr.collect.sync.dao.gc;

import cn.wr.collect.sync.constants.ElasticEnum;
import cn.wr.collect.sync.hbase.HBaseService;
import cn.wr.collect.sync.model.MetricItem;
import cn.wr.collect.sync.model.SplitMiddleData;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.gc.PartnerStoresAll;
import cn.wr.collect.sync.model.partner.PartnerGoods;
import cn.wr.collect.sync.model.partner.PartnerStoreGoods;
import cn.wr.collect.sync.utils.MysqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.wr.collect.sync.constants.CommonConstants.OPERATE_UPDATE;
import static cn.wr.collect.sync.constants.CommonConstants.SCHEMA_UNION_DRUG_PARTNER;

public class ElasticDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticDao.class);

    private static final String GOODS_QUERY_BY_DB_ID = "select `id`, `db_id`, `table_id`, `internal_id`, `common_name`, `trade_code`, " +
            " `approval_number`, `form`, `pack`, `price`, `manufacturer`, `status`, " +
            " (CASE `goods_create_time` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `goods_create_time` END) AS `goods_create_time`, " +
            " (CASE `goods_update_time` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `goods_update_time` END) AS `goods_update_time`, " +
            " (CASE `created_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `created_at` END) AS `created_at`, " +
            " (CASE `updated_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `updated_at` END) AS `updated_at`, " +
            " (CASE `gmtcreated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmtcreated` END) AS `gmtcreated`, " +
            " (CASE `gmtupdated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmtupdated` END) AS `gmtupdated` " +
            " from " + SCHEMA_UNION_DRUG_PARTNER + ".`partner_goods` " +
            " where db_id = ? and id > ? order by id asc limit ?;";


    public List<MetricItem<SplitMiddleData>> query(PartnerStoresAll storesAll,
                                                   Map<String, Object> params,
                                                   Integer pageSize,
                                                   Connection connection,
                                                   HBaseService hBaseService) {
        long start = System.currentTimeMillis();
        if (null == connection) return Collections.emptyList();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Integer dbId = storesAll.getDbId();
            ps = connection.prepareStatement(GOODS_QUERY_BY_DB_ID);
            ps.setInt(1, dbId);
            ps.setLong(2, (long) params.get("id"));
            ps.setInt(3, pageSize);
            rs = ps.executeQuery();

            long end = System.currentTimeMillis();
            LOGGER.info("ElasticDao params:{} query complete 耗时:{}(秒)", dbId, end - start);

            List<MetricItem<SplitMiddleData>> list = new ArrayList<>();
            while (rs.next()) {
                PartnerGoods goods = new PartnerGoods().convert(rs);
                params.put("id", goods.getId());
                if (StringUtils.equals(ElasticEnum.O2O.getChannel(), storesAll.getChannel())) {
                    PartnerStoreGoods storeGoods = hBaseService.queryPartnerStoreGoods(dbId, storesAll.getGroupId(), goods.getInternalId());
                    if (null == storeGoods) {
                        LOGGER.info("ElasticDao query hbase storeGoods empty, dbId:{}, groupId:{}, internalId:{}", dbId, storesAll.getGroupId(), goods.getInternalId());
                        continue;
                    }
                    SplitMiddleData data = new SplitMiddleData(storesAll, goods, storeGoods);
                    MetricItem<SplitMiddleData> item = new MetricItem<>();
                    item.setItem(data);
                    item.setTableName(Table.BaseDataTable.gc_partner_stores_all.name());
                    item.setOperate(OPERATE_UPDATE);
                    list.add(item);
                }
                else {
                    SplitMiddleData data = new SplitMiddleData(storesAll, goods, null);
                    MetricItem<SplitMiddleData> item = new MetricItem<>();
                    item.setItem(data);
                    item.setTableName(Table.BaseDataTable.gc_partner_stores_all.name());
                    item.setOperate(OPERATE_UPDATE);
                    list.add(item);
                }
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("ElasticDao query error:{}", e);
        }
        finally {
            long end = System.currentTimeMillis();
            LOGGER.error("ElasticDao query time:{}(s)", (end - start) / 1000);
            MysqlUtils.close(null, ps, rs);
        }
        return Collections.emptyList();
    }
}
