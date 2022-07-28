package cn.wr.collect.sync.model.alarm;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class GoodsCenterAlarm implements Serializable {
    private static final long serialVersionUID = -1929295019591035650L;
    /**
     * 来源
     * 0: 连锁推送变更
     * 1: 商品中心变更
     */
    private Integer source;
    /**
     * 操作类型
     * insert-新增
     * update-更新
     * delete-删除
     */
    private String operate;
    /**
     * 表名
     */
    private String table;
    /**
     * 连锁id
     */
    private Long merchantId;
    /**
     * 商品内码
     */
    private String goodsInternalId;

    /**
     * 字段
     */
    private List<Field> fieldList;

    public GoodsCenterAlarm(Integer source, String operate, String table, Long merchantId, String goodsInternalId) {
        this.source = source;
        this.operate = operate;
        this.table = table;
        this.merchantId = merchantId;
        this.goodsInternalId = goodsInternalId;
    }

    @Getter
    @Setter
    public static class Field {
        /**
         * 字段名
         */
        private String fieldName;
        /**
         * 旧值
         */
        private String oldVal;
        /**
         * 新值
         */
        private String val;

        public Field(String fieldName, String oldVal, String val) {
            this.fieldName = fieldName;
            this.oldVal = oldVal;
            this.val = val;
        }
    }
}
