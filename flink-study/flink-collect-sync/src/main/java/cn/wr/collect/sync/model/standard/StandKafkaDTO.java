package cn.wr.collect.sync.model.standard;

import lombok.Data;

import java.io.Serializable;

@Data
public class StandKafkaDTO implements Serializable {
  private static final long serialVersionUID = 4116392519004432559L;

  /** 商品名称 */
  private RedisStandard redisStandard;

  /** 商品主类型 */
  private RedisManual redisManual;

}
