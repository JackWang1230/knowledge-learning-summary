package cn.wr.collect.sync.model.middledb;

import cn.wr.collect.sync.model.gc.PgcMerchantInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PgcStoreInfoShortInit implements Serializable {
    private static final long serialVersionUID = -8371947399586758398L;

    private Integer dbId;
    List<PgcStoreInfoShort> infoShortList;

}
