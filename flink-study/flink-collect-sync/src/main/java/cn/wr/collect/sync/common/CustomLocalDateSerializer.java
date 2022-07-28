package cn.wr.collect.sync.common;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonGenerator;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonSerializer;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class CustomLocalDateSerializer extends JsonSerializer<LocalDate> {

    private static final String DATE_FORMATTER = "yyyy-MM-dd";

    @Override
    public void serialize(LocalDate value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (Objects.isNull(value)) {
            jsonGenerator.writeNull();
        } else {
            jsonGenerator.writeString(DateTimeFormatter.ofPattern(DATE_FORMATTER).format(value));
        }
    }
}
