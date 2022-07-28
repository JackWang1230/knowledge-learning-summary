package cn.wr.collect.sync.datasource;

import cn.wr.collect.sync.constants.SymbolConstants;
import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.dao.gc.*;
import cn.wr.collect.sync.dao.partner.*;
import cn.wr.collect.sync.dao.stock.StockGoodsDAO;
import cn.wr.collect.sync.dao.stock.StockMerchantDAO;
import cn.wr.collect.sync.hbase.HBaseService;
import cn.wr.collect.sync.model.BaseDataInitEvent;
import cn.wr.collect.sync.model.InitMap;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.utils.HBaseUtils;
import cn.wr.collect.sync.utils.MysqlUtils;
import cn.wr.collect.sync.utils.QueryUtil;
import cn.wr.collect.sync.utils.RedisPoolUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.wr.collect.sync.constants.CommonConstants.HBASE_PREFIX;
import static cn.wr.collect.sync.constants.PropertiesConstants.COMPLEMENT_CLEAR_HBASE;
import static cn.wr.collect.sync.constants.PropertiesConstants.COMPLEMENT_CLEAR_REDIS;
import static cn.wr.collect.sync.constants.PropertiesConstants.COMPLEMENT_DB_ID;
import static cn.wr.collect.sync.constants.PropertiesConstants.COMPLEMENT_END_TIME;
import static cn.wr.collect.sync.constants.PropertiesConstants.COMPLEMENT_ID;
import static cn.wr.collect.sync.constants.PropertiesConstants.COMPLEMENT_POLAR2CACHE_TABLE;
import static cn.wr.collect.sync.constants.PropertiesConstants.COMPLEMENT_START_TIME;
import static cn.wr.collect.sync.constants.RedisConstant.REDIS_TABLE_PREFIX;



public class BasicInitSource extends RichSourceFunction<BaseDataInitEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicInitSource.class);

    private static final long serialVersionUID = -2842316060200169637L;

    private Connection connection;
    private ParameterTool parameterTool;
    private LocalDateTime complementStartTime = null;
    private LocalDateTime complementEndTime = null;
    private Integer dbId = null;
    private List<String> cacheList = null;
    private boolean clearRedis = false;
    private boolean clearHbase = false;
    private List<Long> idList = null;
    boolean RUNNING = true;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        connection = MysqlUtils.getConnection(parameterTool);
        this.initParams();
    }

    @Override
    public void run(SourceContext<BaseDataInitEvent> sourceContext) {
        if (clearRedis) {
            this.clearRedis();
        }
        if (clearHbase) {
            this.clearHBase();
        }

        List<InitMap> initList = this.initList();
        initList.stream().forEach(e -> collectData(e.getTable(), e.getQueryLimitDao(), sourceContext));
    }

    @Override
    public void cancel() {
        RUNNING = false;
        MysqlUtils.close(connection, null, null);
    }

    /**
     * 初始化参数
     */
    private void initParams() {
        String complementStartTimeStr = parameterTool.get(COMPLEMENT_START_TIME);
        String complementEndTimeStr = parameterTool.get(COMPLEMENT_END_TIME);
        String dbIdStr = parameterTool.get(COMPLEMENT_DB_ID);
        if (StringUtils.isNotBlank(complementStartTimeStr) && StringUtils.isNotBlank(complementEndTimeStr)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            complementStartTime = LocalDateTime.parse(complementStartTimeStr, formatter);
            complementEndTime = LocalDateTime.parse(complementEndTimeStr, formatter);
        }
        if (StringUtils.isNotBlank(dbIdStr)) {
            dbId = Integer.valueOf(dbIdStr);
        }
        String cacheListStr = parameterTool.get(COMPLEMENT_POLAR2CACHE_TABLE);
        if (StringUtils.isNotBlank(cacheListStr)) {
            cacheList = Arrays.asList(cacheListStr.split(SymbolConstants.COMMA_EN));
        }
        clearRedis = parameterTool.getBoolean(COMPLEMENT_CLEAR_REDIS);
        clearHbase = parameterTool.getBoolean(COMPLEMENT_CLEAR_HBASE);
        String ids = parameterTool.get(COMPLEMENT_ID);
        if (StringUtils.isNotBlank(ids)) {
            idList = Arrays.stream(ids.split(SymbolConstants.COMMA_EN)).map(Long::valueOf).collect(Collectors.toList());
        }
    }

    /**
     * 初始化表
     * @return
     */
    private List<InitMap> initList() {
        if (CollectionUtils.isEmpty(cacheList)) {
            return Collections.emptyList();
        }
        List<InitMap> list = new ArrayList<>();
        list.add(new InitMap(Table.BaseDataTable.gc_base_nootc, new BaseNootcDao()));
        list.add(new InitMap(Table.BaseDataTable.merchant_goods_category_mapping, new MerchantGoodsCategoryMappingDao()));
        list.add(new InitMap(Table.BaseDataTable.base_goods, new BaseGoodsDao()));
        list.add(new InitMap(Table.BaseDataTable.gc_base_spu_img, new BaseSpuImgDao()));
        list.add(new InitMap(Table.BaseDataTable.gc_goods_manual, new GoodsManualDao()));
        list.add(new InitMap(Table.BaseDataTable.gc_partner_goods_gift, new PartnerGoodsGiftDao()));
        // gc_partner_stores_all 需全量同步
        list.add(new InitMap(Table.BaseDataTable.gc_partner_stores_all, new PartnerStoresAllDao()));
        list.add(new InitMap(Table.BaseDataTable.partner_goods_img, new PartnerGoodsImgDao()));
        list.add(new InitMap(Table.BaseDataTable.gc_goods_attr_info_syncrds, new GoodsAttrInfoSyncrdsDao()));
        list.add(new InitMap(Table.BaseDataTable.gc_goods_spu_attr_syncrds, new GoodsSpuAttrSyncrdsDao()));
        list.add(new InitMap(Table.BaseDataTable.partner_goods_info, new PartnerGoodsInfoDao()));
        list.add(new InitMap(Table.BaseDataTable.gc_goods_dosage, new GoodsDosageDao()));
        list.add(new InitMap(Table.BaseDataTable.gc_standard_goods_syncrds, new StandardGoodsSyncrdsDao(parameterTool)));
        list.add(new InitMap(Table.BaseDataTable.pgc_store_info, new PgcStoreInfoDAO(parameterTool)));
        list.add(new InitMap(Table.BaseDataTable.pgc_store_info_increment, new PgcStoreInfoIncrementDAO(parameterTool)));
        list.add(new InitMap(Table.BaseDataTable.organize_base, new OrganizeBaseDAO(parameterTool)));
        list.add(new InitMap(Table.BaseDataTable.platform_goods, new PlatformGoodsDAO(parameterTool)));
        list.add(new InitMap(Table.BaseDataTable.partner_goods, new PartnerGoodsDao(parameterTool)));
        list.add(new InitMap(Table.BaseDataTable.partner_store_goods, new PartnerStoreGoodsDao(parameterTool)));
        list.add(new InitMap(Table.BaseDataTable.pgc_merchant_info, new PgcMerchantInfoDAO(parameterTool)));
        list.add(new InitMap(Table.BaseDataTable.gc_goods_overweight, new GoodsOverweightDAO(parameterTool)));
        list.add(new InitMap(Table.BaseDataTable.gc_sku_extend, new SkuExtendDAO(parameterTool)));
        list.add(new InitMap(Table.BaseDataTable.gc_goods_spu, new GoodsSpuDAO(parameterTool)));
        list.add(new InitMap(Table.BaseDataTable.gc_category_info, new CategoryInfoDAO(parameterTool)));
        list.add(new InitMap(Table.BaseDataTable.gc_goods_cate_spu, new GoodsCateSpuDAO(parameterTool)));
        list.add(new InitMap(Table.BaseDataTable.gc_config_sku, new ConfigSkuDAO(parameterTool)));
        list.add(new InitMap(Table.BaseDataTable.partners, new PartnersDAO(parameterTool)));
        list.add(new InitMap(Table.BaseDataTable.stock_goods, new StockGoodsDAO(parameterTool)));
        list.add(new InitMap(Table.BaseDataTable.partner_stores, new PartnerStoresDAO(parameterTool)));
        list.add(new InitMap(Table.BaseDataTable.stock_merchant, new StockMerchantDAO(parameterTool)));

        return list.stream().filter(item -> cacheList.contains(item.getTable().name())).collect(Collectors.toList());
    }

    /**
     * 同步数据
     * @param table
     * @param queryLimitDao
     * @param sourceContext
     */
    private void collectData(Table.BaseDataTable table, QueryLimitDao queryLimitDao, SourceContext<BaseDataInitEvent> sourceContext) {
        // 获取表对应的实体类
        Model model = table.getModel();
        Class<? extends Model> modelClass = model.getClass();
        int i = 0;
        List<Model> models;
        Long id = 0L;
        do {
            // 从数据库查询数据,一次查1w条
            Map<String, Object> params = new HashMap<>();
            try {
                params.put("id", id);
                params.put("dbId", dbId);
                params.put("complementStartTime", complementStartTime);
                params.put("complementEndTime", complementEndTime);
                params.put("idList", idList);
                models = QueryUtil.findLimitPlus(queryLimitDao, i, params, connection);
                // LOGGER.info("BasicInitSource tableName:{} page:{}, size:{}, params:{}", table, i, models.size());
                if (CollectionUtils.isNotEmpty(models)) {
                    BaseDataInitEvent baseDataInitEvent = new BaseDataInitEvent(table.name(), models, modelClass);
                    sourceContext.collect(baseDataInitEvent);
                    id = models.get(models.size() - 1).getId();
                }
            } catch (Exception e) {
                LOGGER.error("BasicInitSource tableName:{}, page:{}, params：{}, error:{}",
                        table.name(), i, params, e);
                models = new ArrayList<>();
            }
            i++;
        } while (RUNNING && CollectionUtils.isEmpty(idList)
                && models.size() == QueryUtil.QUERY_PAGE_SIZE * QueryUtil.its.length);
    }

    /**
     * 清空初始化数据
     */
    private void clearRedis() {
        Jedis jedis = RedisPoolUtil.getInstance(parameterTool);
        cacheList.stream()
                .filter(table -> Arrays.stream(Table.ToHbaseTable.values())
                        .noneMatch(ht -> StringUtils.equals(ht.name(), table)))
                .forEach(table -> {
                    ScanParams sp = new ScanParams();
                    sp.match(REDIS_TABLE_PREFIX + table + SymbolConstants.COLON + "*");
                    sp.count(100000);
                    String cursor = "0";
                    while (RUNNING) {
                        ScanResult<String> scan = jedis.scan(cursor, sp);
                        List<String> result = scan.getResult();
                        cursor = scan.getStringCursor();
                        if (CollectionUtils.isNotEmpty(result)) {
                            Long del = jedis.del(result.toArray(new String[result.size()]));
                            LOGGER.info("BasicInitSource clearRedis table:{} size:{} del:{}", table, result.size(), del);
                        }
                        if ("0".equals(cursor)) {
                            break;
                        }
                    }
                });
        RedisPoolUtil.closeConn(jedis);
    }

    /**
     * 清空hbase
     */
    private void clearHBase() {
        HBaseService hBaseService = new HBaseService(HBaseUtils.getConnection(parameterTool));
        cacheList.stream()
                .filter(table -> Arrays.stream(Table.ToHbaseTable.values())
                        .anyMatch(ht -> StringUtils.equals(ht.name(), table)))
                .forEach(table -> {
                    hBaseService.truncateTable(HBASE_PREFIX + table);
                });
        hBaseService.closeConnection();
    }
}
