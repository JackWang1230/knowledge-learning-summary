package cn.wr.collect.sync.model.standard;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RedisMutex implements Serializable {
	private static final long serialVersionUID = -1683535826530963574L;
	private String approvalNumber;
	private List<RedisMutexRef> mutexRefs;

	@Data
	public static class RedisMutexRef implements Serializable {
		private static final long serialVersionUID = 8494580629812121999L;
		private String approvalNumber;
		private Integer mutexType;
	}
}
