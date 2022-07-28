package cn.wr.collect.sync.dao.relation;

import cn.wr.collect.sync.model.goodsall.GoodsAllData;
import cn.wr.collect.sync.model.goodsall.GoodsAllReq;
import cn.wr.collect.sync.utils.MysqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.wr.collect.sync.constants.CommonConstants.*;
import static cn.wr.collect.sync.constants.PropertiesConstants.*;

public class GoodsAllDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsAllDao.class);


    private ParameterTool parameterTool;

    public GoodsAllDao(ParameterTool parameterTool) {
        this.parameterTool = parameterTool;
    }

    public List<GoodsAllData> listGoodsAll(GoodsAllReq goodsAllReq) {

        StringBuilder stringBuild = new StringBuilder();
        stringBuild.append(" SELECT ");
        stringBuild.append(" 	trade_code, ");
        stringBuild.append(" 	temp.merchant_id, ");
        stringBuild.append(" 	temp.store_id, ");
        stringBuild.append(" 	t9.latitude, ");
        stringBuild.append(" 	t9.longitude  ");
        stringBuild.append(" FROM ");
        stringBuild.append(" 	( ");
        stringBuild.append(" 	SELECT DISTINCT ");
        stringBuild.append(" 		t2.trade_code, ");

        if (!(GC_PARTNER_STORES_ALL.equals(goodsAllReq.getTableName()) && OPERATE_DELETE.equals(goodsAllReq.getOperate()))) {
            stringBuild.append(" 		t1.merchant_id, ");
            stringBuild.append(" 		t1.store_id  ");
        } else {
            stringBuild.append(goodsAllReq.getGcPartnerStoresAllMerchantId() + " as merchant_id, ");
            stringBuild.append(goodsAllReq.getGcPartnerStoresAllStoreId() + " as store_id ");
        }
        stringBuild.append(" 	FROM ");

        // gc_partner_stores_all表的删除操作不需要关联
        if (!(GC_PARTNER_STORES_ALL.equals(goodsAllReq.getTableName()) && OPERATE_DELETE.equals(goodsAllReq.getOperate()))) {
            stringBuild.append(" 		gc_partner_stores_all t1, ");
        }
        stringBuild.append(" 		uniondrug_partner.partner_goods t2, ");
        stringBuild.append(" 		uniondrug_partner.partner_store_goods t3, ");
        stringBuild.append(" 		gc_standard_goods_syncrds t4  ");
        stringBuild.append(" 	WHERE ");
        stringBuild.append(" 		1 = 1  ");
        if (!(OPERATE_DELETE.equals(goodsAllReq.getOperate()) && GC_PARTNER_STORES_ALL.equals(goodsAllReq.getTableName()))) {
            stringBuild.append(" 		AND t1.db_id = t2.db_id  ");
            stringBuild.append(" 		AND t1.group_id = t3.group_id  ");
        }
        stringBuild.append(" 		AND t2.db_id = t3.db_id  ");
        stringBuild.append(" 		AND t2.internal_id = t3.goods_internal_id  ");
        stringBuild.append(" 		AND t2.trade_code = t4.trade_code  ");



        /*if(Objects.nonNull(goodsAllReq.getOrganizeBaseStoreId())){
            stringBuild.append(" 		AND t4.organizationId = ").append(goodsAllReq.getOrganizeBaseStoreId());
        }*/
        if (Objects.nonNull(goodsAllReq.getGcGoodsSpuAttrSyncrdsSpuId())) {
            stringBuild.append(" 		AND t4.spu_id = ").append(goodsAllReq.getGcGoodsSpuAttrSyncrdsSpuId());
        }

        if (Objects.nonNull(goodsAllReq.getGcPartnerStoresAllMerchantId()) && !(GC_PARTNER_STORES_ALL.equals(goodsAllReq.getTableName()) && OPERATE_DELETE.equals(goodsAllReq.getOperate()))) {
            stringBuild.append(" 		AND t1.merchant_id = ").append(goodsAllReq.getGcPartnerStoresAllMerchantId());
        }
        if (Objects.nonNull(goodsAllReq.getGcPartnerStoresAllStoreId()) && !(GC_PARTNER_STORES_ALL.equals(goodsAllReq.getTableName()) && OPERATE_DELETE.equals(goodsAllReq.getOperate()))) {
            stringBuild.append(" 		AND t1.store_id = ").append(goodsAllReq.getGcPartnerStoresAllStoreId());
        }
        if (StringUtils.isNotBlank(goodsAllReq.getGcPartnerStoresAllChannel()) && !(GC_PARTNER_STORES_ALL.equals(goodsAllReq.getTableName()) && OPERATE_DELETE.equals(goodsAllReq.getOperate()))) {
            stringBuild.append(" 		AND t1.channel = '").append(goodsAllReq.getGcPartnerStoresAllChannel()).append("'");
        }
        if (Objects.nonNull(goodsAllReq.getPartnerStoreGoodsDbId())) {
            stringBuild.append(" 		AND t3.db_id = ").append(goodsAllReq.getPartnerStoreGoodsDbId());
        }
        if (StringUtils.isNotBlank(goodsAllReq.getPartnerStoreGoodsGroupId())) {
            stringBuild.append(" 		AND t3.group_id = ").append(goodsAllReq.getPartnerStoreGoodsGroupId());
        }
        if (StringUtils.isNotBlank(goodsAllReq.getGcStandardGoodsSyncrdsTradeCode())) {
            stringBuild.append(" 		AND t4.trade_code = '").append(goodsAllReq.getGcStandardGoodsSyncrdsTradeCode()).append("'");
        }
        stringBuild.append(" 		AND t2.`status` = 1  ");
        stringBuild.append(" 		AND t3.`status` = 1  ");
        stringBuild.append(" 		AND t3.`price` >= 0.1  ");

        stringBuild.append(" 		AND NOT EXISTS ( ");
        stringBuild.append(" 		SELECT ");
        stringBuild.append(" 			t5.STATUS  ");
        stringBuild.append(" 		FROM ");
        stringBuild.append(" 			platform_goods t5  ");
        stringBuild.append(" 		WHERE ");
        if (!(GC_PARTNER_STORES_ALL.equals(goodsAllReq.getTableName()) && OPERATE_DELETE.equals(goodsAllReq.getOperate()))) {
            stringBuild.append(" 			t5.merchantId = t1.merchant_id  ");
            stringBuild.append(" 			AND t5.storeId = t1.store_id  ");
            stringBuild.append(" 			AND t5.channel = t1.channel  ");
        } else {
            stringBuild.append(" 			t5.merchantId =   " + goodsAllReq.getGcPartnerStoresAllMerchantId());
            stringBuild.append(" 			AND t5.storeId =   " + goodsAllReq.getGcPartnerStoresAllStoreId());
            stringBuild.append(" 			AND t5.channel =   '" + goodsAllReq.getGcPartnerStoresAllChannel() + "'");
        }
        stringBuild.append(" 			AND t5.goodsInternalId = t2.internal_id  ");

        stringBuild.append(" 			AND t5.STATUS = 1  ");
        stringBuild.append(" 		)  ");
        if (!(OPERATE_DELETE.equals(goodsAllReq.getOperate()) && ORGANIZE_BASE.equals(goodsAllReq.getTableName()))) {

            stringBuild.append(" 		AND EXISTS ( ");
            stringBuild.append(" 		SELECT ");
            stringBuild.append(" 			*  ");
            stringBuild.append(" 		FROM ");
            stringBuild.append(" 			organize_base t6  ");
            stringBuild.append(" 		WHERE ");

            stringBuild.append(" 			t6.isO2O = 1  ");
            stringBuild.append(" 			AND t6.netType <> 2  ");
            if (!(GC_PARTNER_STORES_ALL.equals(goodsAllReq.getTableName()) && OPERATE_DELETE.equals(goodsAllReq.getOperate()))) {
                stringBuild.append(" 			AND t6.rootId = t1.merchant_id  ");
                stringBuild.append(" 			AND t6.organizationId = t1.store_id  ");
            } else {
                stringBuild.append(" 			AND t6.rootId =  ").append(goodsAllReq.getGcPartnerStoresAllMerchantId());
                stringBuild.append(" 			AND t6.organizationId =   ").append(goodsAllReq.getGcPartnerStoresAllStoreId());
            }
            stringBuild.append(" 		)  ");
        }
        stringBuild.append(" 		AND NOT EXISTS ( ");
        stringBuild.append(" 		SELECT ");
        stringBuild.append(" 			spu_id  ");
        stringBuild.append(" 		FROM ");
        stringBuild.append(" 			gc_goods_spu_attr_syncrds t7  ");
        stringBuild.append(" 		WHERE ");
        stringBuild.append(" 			t7.spu_id IS NOT NULL  ");
        stringBuild.append(" 			AND t7.attr_id IN ( 31002, 31003, 31004 )  ");
        stringBuild.append(" 			AND t7.spu_id = t4.spu_id  ");
        stringBuild.append(" 		)  ");
        if (!(GC_GOODS_OVERWEIGHT.equals(goodsAllReq.getTableName()) && OPERATE_DELETE.equals(goodsAllReq.getOperate()))) {
            stringBuild.append(" 		AND NOT EXISTS ( SELECT * FROM gc_goods_overweight t8 WHERE t8.trade_code = t4.trade_code AND t8.is_overweight = 1 )  ");
        }
        stringBuild.append(" 	) temp ");
        stringBuild.append(" 	LEFT JOIN pgc_store_info_increment t9 ON temp.store_id = t9.store_id AND temp.merchant_id = t9.organization_id ");

        List<GoodsAllData> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_GOODSCENTER_URL),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_PASSWORD));

            ps = connection.prepareStatement(stringBuild.toString());
            System.out.println("goodsAllSql: " + stringBuild.toString());

            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new GoodsAllData().convert(rs));
            }

            return list;
        } catch (Exception e) {
            LOGGER.error("### GoodsAllDao listGoodsAll params:{} Exception:{}", goodsAllReq.toString(), e);
        } finally {
            LOGGER.info("### GoodsAllDao listGoodsAll params:{} size:{}", goodsAllReq.toString(), list.size());
            MysqlUtils.close(connection, ps, rs);
            return list;
        }
    }


}
