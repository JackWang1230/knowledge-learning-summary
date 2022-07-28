package cn.wr.collect.sync.function.consumer;

import cn.wr.collect.sync.model.ElasticO2O;
import cn.wr.collect.sync.model.annotations.Correspond;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Consumer;

public class Single implements Consumer<Field> {

    @Override
    public void accept(Field correspondField) {
        Field[] o2oField = ElasticO2O.class.getDeclaredFields();
        Correspond correspond = correspondField.getDeclaredAnnotation(Correspond.class);
        Arrays.stream(correspond.field()).forEach(esfield -> {
            for (Field f : o2oField) {
                if (!f.isAnnotationPresent(JSONField.class)) {
                    continue;
                }
                JSONField jsonField = f.getDeclaredAnnotation(JSONField.class);
                if (StringUtils.isBlank(esfield) || !StringUtils.equals(esfield, jsonField.name())) {
                    continue;
                }

                break;
            }
        });
    }
}
