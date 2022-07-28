package cn.wr.collect.sync.common;


import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonParser;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonToken;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class CustomLocalDateDeserializer extends JsonDeserializer<LocalDate> {

    private static final String DATE_FORMATTER = "yyyy-MM-dd";

    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_STRING) {
            String str = jp.getText().trim();
            return LocalDate.parse(str, DateTimeFormatter.ofPattern(DATE_FORMATTER));
        }
        if (t == JsonToken.VALUE_NUMBER_INT) {
            return new Date(jp.getLongValue()).toLocalDate();
        }
        throw ctxt.mappingException(handledType());
    }
}
