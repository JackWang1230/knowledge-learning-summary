package cn.wr.collect.sync.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PgConcatParams {
    /**
     * spuId
     */
    private Long spuId;
    /**
     * 批转文号
     */
    private String approvalNumber;
    /**
     * 条码
     */
    private String tradeCode;
    /**
     * 连锁dbid
     */
    private String dbId;
    /**
     * 商品id
     */
    private String internalId;
    /**
     * 连锁id
     */
    private String merchantId;
    /**
     * 门店id
     */
    private String storeId;

    /**
     * 经纬度
     */
    private String location;

    /**
     * 价格组
     */
    private String groupId;

    public PgConcatParams(String tradeCode) {
        this.tradeCode = tradeCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PgConcatParams params = (PgConcatParams) o;

        if (!Objects.equals(spuId, params.spuId)) return false;
        if (!Objects.equals(approvalNumber, params.approvalNumber))
            return false;
        if (!Objects.equals(tradeCode, params.tradeCode)) return false;
        if (!Objects.equals(dbId, params.dbId)) return false;
        if (!Objects.equals(internalId, params.internalId)) return false;
        if (!Objects.equals(merchantId, params.merchantId)) return false;
        if (!Objects.equals(storeId, params.storeId)) return false;
        if (!Objects.equals(location, params.location)) return false;
        return Objects.equals(groupId, params.groupId);
    }

    @Override
    public int hashCode() {
        int result = spuId != null ? spuId.hashCode() : 0;
        result = 31 * result + (approvalNumber != null ? approvalNumber.hashCode() : 0);
        result = 31 * result + (tradeCode != null ? tradeCode.hashCode() : 0);
        result = 31 * result + (dbId != null ? dbId.hashCode() : 0);
        result = 31 * result + (internalId != null ? internalId.hashCode() : 0);
        result = 31 * result + (merchantId != null ? merchantId.hashCode() : 0);
        result = 31 * result + (storeId != null ? storeId.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (groupId != null ? groupId.hashCode() : 0);
        return result;
    }
}
