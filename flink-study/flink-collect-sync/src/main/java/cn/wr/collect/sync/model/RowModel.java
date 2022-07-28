package cn.wr.collect.sync.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RowModel<T> {

    T newValue;

    T oldValue;

}
