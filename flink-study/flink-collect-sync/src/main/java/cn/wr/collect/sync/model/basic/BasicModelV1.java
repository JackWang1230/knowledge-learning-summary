package cn.wr.collect.sync.model.basic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicModelV1<T> extends BasicModel<T> {

    private static final long serialVersionUID = 2891618220315018528L;

    private List<String> esModFields;
}
