package cn.wr.collect.sync.model.standard;

import lombok.Data;

import java.io.Serializable;

@Data
public class RedisAttrs implements Serializable {
  private static final long serialVersionUID = 9080695003752327291L;

  private String title;
  private String id;
  private String pids;

}
