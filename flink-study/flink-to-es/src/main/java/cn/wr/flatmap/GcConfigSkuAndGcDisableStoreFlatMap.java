package cn.wr.flatmap;


import cn.wr.model.*;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.wr.constants.PropertiesConstants.*;

/**
 * @author RWang
 * @Date 2022/5/16
 */

public class GcConfigSkuAndGcDisableStoreFlatMap extends RichFlatMapFunction<CanalDataModel, BaseJudge> {

    private static final long serialVersionUID = 8811911961753934931L;

    private static final String CONTROL_STATUS ="control_status";
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public void flatMap(CanalDataModel value, Collector<BaseJudge> out) throws Exception {
        String table = value.getTable();

        switch (table){
            case GC_CONFIG_SKU:
                List<BaseJudge> baseJudges = dealGcConfigSku(value);
                collectBaseJudge(baseJudges,out);
                break;
            case GC_DISABLE_STORE:
                List<BaseJudge> baseJudges1 = dealGcDisableStore(value);
                collectBaseJudge(baseJudges1,out);
                break;
            default:
                break;
        }

    }


    /**
     *  处理gc_config_sku这张表的binlog
     *
     *  判断 control_status
     *            1)  0->2
     *            2)  2->0
     * @param value
     */
    public List<BaseJudge> dealGcConfigSku(CanalDataModel value) throws JsonProcessingException {
        ArrayList<BaseJudge> baseJudges = new ArrayList<>();

        if (value.getType().toUpperCase().equals(UPDATE)) {

            List<Object> data = value.getData();
            List<Object> old = value.getOld();
            for (int i = 0; i < old.size(); i++) {
                JSONObject jsonOldObject = JSONObject.parseObject(objectMapper.writeValueAsString(old.get(i)));
                if (!jsonOldObject.containsKey(CONTROL_STATUS)) {
                    continue;
                }
                GcConfigSku gcConfigSku = objectMapper.convertValue(data.get(i), GcConfigSku.class);
                GcConfigJudge judgeStatus = objectMapper.convertValue(old.get(i), GcConfigJudge.class);
                judgeStatus.setSkuNo(gcConfigSku.getSkuNo());
                judgeStatus.setTableName(value.getTable());
                judgeStatus.setMerchantId(gcConfigSku.getMerchantId());
                judgeStatus.setInternalId(gcConfigSku.getExternalCode());
                judgeStatus.setNewControlStatus(gcConfigSku.getControlStatus());
                judgeStatus.setOperate(UPDATE);
                baseJudges.add(judgeStatus);
                return baseJudges;
            }
        }
        if (value.getType().toUpperCase().equals(INSERT)){
            List<Object> data = value.getData();
            for (Object datum : data) {
                GcConfigSku gcConfigSku = objectMapper.convertValue(datum, GcConfigSku.class);
                GcConfigJudge judgeStatus = new GcConfigJudge();
                judgeStatus.setInternalId(gcConfigSku.getExternalCode());
                judgeStatus.setSkuNo(gcConfigSku.getSkuNo());
                judgeStatus.setMerchantId(gcConfigSku.getMerchantId());
                judgeStatus.setTableName(value.getTable());
                judgeStatus.setOperate(INSERT);
                judgeStatus.setNewControlStatus(gcConfigSku.getControlStatus());
                judgeStatus.setOldControlStatus(gcConfigSku.getControlStatus());
                judgeStatus.setMerchantId(gcConfigSku.getMerchantId());
                baseJudges.add(judgeStatus);
                return baseJudges;
            }
        }
        return null;

    }

    /**
     * 处理gc_disable_store这张表的binlog
     *  判断 type
     *         1) insert
     *         2) delete
     * @param value
     */
    public List<BaseJudge>  dealGcDisableStore(CanalDataModel value){

        ArrayList<BaseJudge> baseJudges = new ArrayList<>();
        List<Object> data = value.getData();
        String table = value.getTable();
        if (value.getType().toUpperCase().equals(DELETE)){
            return addGcDisableJudge(baseJudges,data,table,DELETE);
        }
        if (value.getType().toUpperCase().equals(INSERT)){
            return addGcDisableJudge(baseJudges,data,table,INSERT);
        }

        return null;

    }

    /**
     *  新增gc_disable_judge判断逻辑
     * @param baseJudges
     * @param data
     * @param table
     * @param operate
     * @return
     */
    public List<BaseJudge> addGcDisableJudge(ArrayList<BaseJudge> baseJudges,
                                             List<Object> data ,String table,String operate){
        for (Object datum : data) {
            GcDisableJudge gcDisableJudge = new GcDisableJudge();
            GcDisableStore gcDisableStore = objectMapper.convertValue(datum, GcDisableStore.class);
            gcDisableJudge.setInternalId(gcDisableStore.getInternalId());
            gcDisableJudge.setStoreId(gcDisableStore.getStoreId());
            gcDisableJudge.setMerchantId(gcDisableJudge.getMerchantId());
            gcDisableJudge.setOperate(operate);
            gcDisableJudge.setTableName(table);
            gcDisableJudge.setSkuNo(gcDisableStore.getSkuNo());
            baseJudges.add(gcDisableJudge);
            return baseJudges;
        }
        return null;
    }

    /**
     * 收集gc_disable_store状态数据
     * @param baseJudges
     * @param out
     */
    public void collectBaseJudge(List<BaseJudge> baseJudges,Collector<BaseJudge> out){
        if (Objects.isNull(baseJudges)) {
            return;
        }
        for (BaseJudge baseJudge : baseJudges) {
            out.collect(baseJudge);
        }
    }

}
