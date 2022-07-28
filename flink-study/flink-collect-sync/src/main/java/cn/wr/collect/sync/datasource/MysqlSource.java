package cn.wr.collect.sync.datasource;

import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.gc.StandardGoodsSyncrds;
import cn.wr.collect.sync.utils.MysqlUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static cn.wr.collect.sync.constants.CommonConstants.SCHEMA_GOODS_CENTER_TIDB;
import static cn.wr.collect.sync.constants.PropertiesConstants.MYSQL_DATABASE_DATACENTER_PASSWORD;
import static cn.wr.collect.sync.constants.PropertiesConstants.MYSQL_DATABASE_DATACENTER_URL;
import static cn.wr.collect.sync.constants.PropertiesConstants.MYSQL_DATABASE_DATACENTER_USERNAME;

public class MysqlSource extends RichSourceFunction<Model> {
    private static final long serialVersionUID = -6179588769980988322L;
    private static final Logger log = LoggerFactory.getLogger(BasicInitSource.class);
    private ParameterTool parameterTool;
    private boolean flag = true;
    private static final String SQL = "select `id`, `spu_id`, `goods_name`, `trade_code`, `specification`, `retail_price`," +
            " `gross_weight`, `origin_place`, `brand`, `category_id`, `search_keywords`, `search_association_words`, " +
            " `sub_title`, `highlights`, `details_code`, `urls`, `status`, `gmtCreated`, `gmtUpdated` " +
            " from " + SCHEMA_GOODS_CENTER_TIDB + ".`gc_standard_goods` " +
            " where `id` > ? " +
            " order by `id` asc limit ?;";

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();

    }

    @Override
    public void run(SourceContext<Model> context) {
        Long id = 0L;
        int pageSize = 10;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        while (flag) {
            try {
                boolean hasNext = false;
                connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_DATACENTER_URL),
                        parameterTool.get(MYSQL_DATABASE_DATACENTER_USERNAME),
                        parameterTool.get(MYSQL_DATABASE_DATACENTER_PASSWORD));

                ps = connection.prepareStatement(SQL);
                ps.setLong(1, id);
                ps.setInt(2, pageSize);
                rs = ps.executeQuery();
                while (rs.next()) {
                    StandardGoodsSyncrds goodsSyncrds = new StandardGoodsSyncrds().convert(rs);
                    context.collect(goodsSyncrds);
                    id = goodsSyncrds.getId();
                    hasNext = true;
                }
                if (!hasNext) {
                    break;
                }
            } catch (Exception e) {
                log.error("Mysql source Exception:{}", e);
            }
            finally {
                MysqlUtils.close(connection, ps, rs);
            }
        }
    }

    @Override
    public void cancel() {
        flag = false;
    }

}
