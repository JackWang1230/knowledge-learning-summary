package cn.wr.collect.sync.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetricEvent<T> implements Serializable {
	private static final long serialVersionUID = -1408317771283166037L;

	/**
	 * Metric fields
	 */
	private List<MetricItem<T>> fields;

	public List<MetricItem<T>> getFields() {
		return fields;
	}

	public void setFields(List<MetricItem<T>> fields) {
		this.fields = fields;
	}
}
