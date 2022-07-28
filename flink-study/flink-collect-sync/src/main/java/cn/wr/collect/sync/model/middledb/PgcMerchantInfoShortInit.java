package cn.wr.collect.sync.model.middledb;

import cn.wr.collect.sync.model.redis.DbMerchant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PgcMerchantInfoShortInit implements Serializable {
    private static final long serialVersionUID = -8371947399586758398L;

    private Integer dbId;
    private List<DbMerchant> merchantInfos;
    private String goodsInternalId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

}
