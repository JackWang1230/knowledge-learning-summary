package cn.wr.collect.sync.datasource;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.dao.partner.PartnersDAO;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.partner.Partners;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DbMerchantInitSource extends RichSourceFunction<BasicModel<Model>> {
    private static final long serialVersionUID = -3872197491712506539L;
    private static final Logger log = LoggerFactory.getLogger(DbMerchantInitSource.class);
    private PartnersDAO partnersDAO;
    private static boolean EXIT_FLAG = true;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        final ParameterTool tool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        partnersDAO = new PartnersDAO(tool);
    }

    @Override
    public void run(SourceContext<BasicModel<Model>> context) throws Exception {
        try {
            int pageSize = 1000;
            Map<String, Object> params = new HashMap<>();
            params.put("id", 0L);
            while (EXIT_FLAG) {
                List<Partners> partnersList = partnersDAO.findLimit(0, pageSize, params, null);
                partnersList.stream()
                        .map(partners -> new BasicModel<Model>(Table.BaseDataTable.partners.name(),
                                CommonConstants.OPERATE_INSERT, partners))
                        .forEach(context::collect);

                if (CollectionUtils.isEmpty(partnersList) || partnersList.size() != pageSize) {
                    EXIT_FLAG = false;
                    break;
                }
                params.put("id", partnersList.get(partnersList.size() - 1).getId());
            }
        }
        catch (Exception e) {
            log.error("DbMerchantInitSource msg:{}, exception:{}", e.getMessage(), e);
        }
    }

    @Override
    public void cancel() {
        EXIT_FLAG = false;
    }
}
