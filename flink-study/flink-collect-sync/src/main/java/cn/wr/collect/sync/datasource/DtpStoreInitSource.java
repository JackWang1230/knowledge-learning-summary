package cn.wr.collect.sync.datasource;

import cn.wr.collect.sync.dao.dtpstore.DtpStoreDAO;
import cn.wr.collect.sync.model.DtpStore;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.util.List;


public class DtpStoreInitSource extends RichSourceFunction<DtpStore> {
    private static final long serialVersionUID = -3872197491712506539L;
    private DtpStoreDAO dtpStoreDAO;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        final ParameterTool tool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        dtpStoreDAO = new DtpStoreDAO(tool);
    }

    @Override
    public void run(SourceContext<DtpStore> context) throws Exception {
        List<DtpStore> dtpStores = dtpStoreDAO.queryList(null);
        dtpStores.forEach(dtpStore -> context.collect(dtpStore));
    }

    @Override
    public void cancel() {
    }
}
