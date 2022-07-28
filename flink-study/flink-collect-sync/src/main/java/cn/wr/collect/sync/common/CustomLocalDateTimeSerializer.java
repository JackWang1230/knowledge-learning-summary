package cn.wr.collect.sync.common;


import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonGenerator;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonSerializer;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class CustomLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    private static final String DATE_TIME_FORMATTER_MILLI = "yyyy-MM-dd HH:mm:ss";

    @Override
    public void serialize(LocalDateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        if (value == null) {
            jgen.writeNull();
        } else {
            jgen.writeString(DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER_MILLI).format(value));
        }
    }
}
