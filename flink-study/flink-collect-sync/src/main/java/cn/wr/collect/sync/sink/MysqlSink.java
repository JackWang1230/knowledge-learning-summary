package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.gc.StandardGoodsSyncrds;
import cn.wr.collect.sync.utils.MysqlUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.Objects;

import static cn.wr.collect.sync.constants.CommonConstants.SCHEMA_GOODS_CENTER_TIDB;


public class MysqlSink extends RichSinkFunction<Model> {
    private static final long serialVersionUID = -7636189588141891300L;
    private static final Logger log = LoggerFactory.getLogger(MysqlSink.class);
    private ParameterTool parameterTool = null;
    private static final String UPDATE_SQL = "INSERT INTO " + SCHEMA_GOODS_CENTER_TIDB + ".`gc_standard_goods_syncrds` " +
            "(`id`, `spu_id`, `goods_name`, `trade_code`, `specification`, `retail_price`," +
            "`gross_weight`, `origin_place`, `brand`, `category_id`, `search_keywords`, `search_association_words`, " +
            "`sub_title`, `highlights`, `details_code`, `urls`, `status`, `gmtCreated`, `gmtUpdated`) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" +
            "ON DUPLICATE KEY UPDATE " +
            "`id` = VALUES(`id`), " +
            "`spu_id` = VALUES(`spu_id`), " +
            "`goods_name` = VALUES(`goods_name`), " +
            "`trade_code` = VALUES(`trade_code`), " +
            "`specification` = VALUES(`specification`), " +
            "`retail_price` = VALUES(`retail_price`), " +
            "`gross_weight` = VALUES(`gross_weight`), " +
            "`origin_place` = VALUES(`origin_place`), " +
            "`brand` = VALUES(`brand`), " +
            "`category_id` = VALUES(`category_id`), " +
            "`search_keywords` = VALUES(`search_keywords`), " +
            "`search_association_words` = VALUES(`search_association_words`), " +
            "`sub_title` = VALUES(`sub_title`), " +
            "`highlights` = VALUES(`highlights`), " +
            "`details_code` = VALUES(`details_code`), " +
            "`urls` = VALUES(`urls`), " +
            "`status` = VALUES(`status`), " +
            "`gmtCreated` = VALUES(`gmtCreated`), " +
            "`gmtUpdated` = VALUES(`gmtUpdated`)"
            ;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void invoke(Model value, Context context) throws Exception {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = MysqlUtils.getConnection(parameterTool);
            ps = connection.prepareStatement(UPDATE_SQL);
            StandardGoodsSyncrds goods = (StandardGoodsSyncrds) value;
            if (Objects.nonNull(goods.getId())) {
                ps.setLong(1, goods.getId());
            } else {
                ps.setNull(1, Types.NULL);
            }
            if (Objects.nonNull(goods.getSpuId())) {
                ps.setLong(2, goods.getSpuId());
            } else {
                ps.setNull(2, Types.NULL);
            }
            if (Objects.nonNull(goods.getGoodsName())) {
                ps.setString(3, goods.getGoodsName());
            } else {
                ps.setNull(3, Types.NULL);
            }
            if (Objects.nonNull(goods.getTradeCode())) {
                ps.setString(4, goods.getTradeCode());
            } else {
                ps.setNull(4, Types.NULL);
            }
            if (Objects.nonNull(goods.getSpecification())) {
                ps.setString(5, goods.getSpecification());
            } else {
                ps.setNull(5, Types.NULL);
            }
            if (Objects.nonNull(goods.getRetailPrice())) {
                ps.setDouble(6, goods.getRetailPrice());
            } else {
                ps.setNull(6, Types.NULL);
            }
            if (Objects.nonNull(goods.getGrossWeight())) {
                ps.setString(7, goods.getGrossWeight());
            } else {
                ps.setNull(7, Types.NULL);
            }
            if (Objects.nonNull(goods.getOriginPlace())) {
                ps.setString(8, goods.getOriginPlace());
            } else {
                ps.setNull(8, Types.NULL);
            }
            if (Objects.nonNull(goods.getBrand())) {
                ps.setString(9, goods.getBrand());
            } else {
                ps.setNull(9, Types.NULL);
            }
            if (Objects.nonNull(goods.getCategoryId())) {
                ps.setLong(10, goods.getCategoryId());
            } else {
                ps.setNull(10, Types.NULL);
            }
            if (Objects.nonNull(goods.getSearchKeywords())) {
                ps.setString(11, goods.getSearchKeywords());
            } else {
                ps.setNull(11, Types.NULL);
            }
            if (Objects.nonNull(goods.getSearchAssociationWords())) {
                ps.setString(12, goods.getSearchAssociationWords());
            } else {
                ps.setNull(12, Types.NULL);
            }
            if (Objects.nonNull(goods.getSubTitle())) {
                ps.setString(13, goods.getSubTitle());
            } else {
                ps.setNull(13, Types.NULL);
            }
            if (Objects.nonNull(goods.getHighlights())) {
                ps.setString(14, goods.getHighlights());
            } else {
                ps.setNull(14, Types.NULL);
            }
            if (Objects.nonNull(goods.getDetailsCode())) {
                ps.setString(15, goods.getDetailsCode());
            } else {
                ps.setNull(15, Types.NULL);
            }
            if (Objects.nonNull(goods.getUrls())) {
                ps.setString(16, goods.getUrls());
            } else {
                ps.setNull(16, Types.NULL);
            }
            if (Objects.nonNull(goods.getStatus())) {
                ps.setInt(17, goods.getStatus());
            } else {
                ps.setNull(17, Types.NULL);
            }
            if (Objects.nonNull(goods.getGmtCreated())) {
                ps.setObject(18, goods.getGmtCreated());
            } else {
                ps.setNull(18, Types.NULL);
            }
            if (Objects.nonNull(goods.getGmtUpdated())) {
                ps.setObject(19, goods.getGmtUpdated());
            } else {
                ps.setNull(19, Types.NULL);
            }

            int i = ps.executeUpdate();
            log.info("Mysql sink num:{}", i);
        } catch (Exception e) {
            log.error("Mysql sink Exception:{}", e);
        }
        finally {
            MysqlUtils.close(connection, ps, null);
        }
    }
}
