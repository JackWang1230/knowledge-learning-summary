package cn.wr.collect.sync.common;


import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonParser;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonToken;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class CustomLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    private static final String DATE_TIME_FORMATTER_MILLI = "yyyy-MM-dd HH:mm:ss.SSS";

    private static final String DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";

    @Override
    public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_STRING) {
            String str = jp.getText().trim();
            if (DATE_TIME_FORMATTER.length() == str.length()) {
                return LocalDateTime.parse(str, DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER));
            } else if (DATE_TIME_FORMATTER_MILLI.length() == str.length()) {
                return LocalDateTime.parse(str, DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER_MILLI));
            }
        }
        if (t == JsonToken.VALUE_NUMBER_INT) {
            return new Timestamp(jp.getLongValue()).toLocalDateTime();
        }
        throw ctxt.mappingException(handledType());
    }

}
