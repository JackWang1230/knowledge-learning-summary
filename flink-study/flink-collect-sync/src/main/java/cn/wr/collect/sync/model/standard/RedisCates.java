package cn.wr.collect.sync.model.standard;

import lombok.Data;

import java.io.Serializable;

@Data
public class RedisCates implements Serializable {
  private static final long serialVersionUID = -3560632649990870336L;
  private String title;
  private String id;
  private String pids;

}
