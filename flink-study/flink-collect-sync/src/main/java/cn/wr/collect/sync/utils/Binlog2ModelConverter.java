package cn.wr.collect.sync.utils;

import cn.wr.collect.sync.model.RowModel;
import cn.wr.collect.sync.model.annotations.Column;
import cn.wr.collect.sync.model.gc.PlatformGoods;
import cn.wr.collect.sync.model.partner.PartnerGoods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class Binlog2ModelConverter {

    public static List<PartnerGoods> convertToGoodses(TiDbDrainerBinlog.Table table) {
        return table.getMutationsList().stream().map(m -> convertToGoods(table.getColumnInfoList(), m.getRow())).collect(Collectors.toList());
    }

    public static PartnerGoods convertToGoods(List<TiDbDrainerBinlog.ColumnInfo> columnInfos, TiDbDrainerBinlog.Row row) {
        return fillModelValuesByColumns(columnInfos, row.getColumnsList(), new PartnerGoods());
    }

    public static PlatformGoods convertToPlatformGoods(List<TiDbDrainerBinlog.ColumnInfo> columnInfos, TiDbDrainerBinlog.Row row) {
        return fillModelValuesByColumns(columnInfos, row.getColumnsList(), new PlatformGoods());
    }

    /**
     * <p>根据Binlog Row的信息，将值填充到对应的Model中
     *
     * @param columnInfos Table中字段信息列表
     * @param mutation  一行的修改记录
     * @param rowModel 本行的数据
     * @param <T>
     * @return Pair<T, T>
     */
    public static <T> RowModel<T> fillModelValuesByMutation(List<TiDbDrainerBinlog.ColumnInfo> columnInfos, TiDbDrainerBinlog.TableMutation mutation, RowModel<T> rowModel) {

        rowModel.setNewValue(fillModelValuesByColumns(columnInfos, mutation.getRow().getColumnsList(), rowModel.getNewValue()));
        rowModel.setOldValue(fillModelValuesByColumns(columnInfos, mutation.getChangeRow().getColumnsList(), rowModel.getOldValue()));

        log.debug("get converted row model: {}", rowModel);
        return rowModel;
    }

    public static <T> T fillModelValuesByColumns(List<TiDbDrainerBinlog.ColumnInfo> columnInfos, List<TiDbDrainerBinlog.Column> columns, T model) {

        Map<TiDbDrainerBinlog.ColumnInfo, TiDbDrainerBinlog.Column> columnAll = IntStream.range(0, columnInfos.size())
                .boxed().collect(Collectors.toMap(columnInfos::get, columns::get));

        List<Field> fields = FieldUtils.getFieldsListWithAnnotation(model.getClass(), Column.class);

        List<ColumnFieldInfo> columnFieldTuples
                = matchColumnsAndModelFields(columnAll, fields);

        fillModelFieldsValues(columnFieldTuples, model);

        return model;
    }

    private static List<ColumnFieldInfo> matchColumnsAndModelFields(
            Map<TiDbDrainerBinlog.ColumnInfo, TiDbDrainerBinlog.Column> columnAll,
            List<Field> fields
    ) {
        return IteratorUtils.zip(columnAll.entrySet(), fields, (col, field) -> {
            Column coa = field.getAnnotation(Column.class);
            return coa.name().equals(col.getKey().getName());
        }, (c, fls) -> {
            Field fd = CollectionUtils.isEmpty(fls) ? null : fls.get(0);
            return new ColumnFieldInfo(c.getKey(), c.getValue(), fd);
        });
    }

    private static <T> void fillModelFieldsValues(List<ColumnFieldInfo> columnFieldTuples, T model) {
        for (ColumnFieldInfo cfi : columnFieldTuples) {
            Object value = getColumnValue(cfi.getColumnInfo(), cfi.getColumn());

            log.debug("convert column field info: {}. value: {}", cfi.getColumnInfo(), value);

            if (Objects.isNull(value) || "".equals(value))
                continue;

            if (Objects.isNull(cfi.getField()))
                continue;

            if (cfi.getField().getType().equals(LocalDateTime.class)) {
                try {
                    value = LocalDateTimeConverter.convert2Time(String.valueOf(value));
                } catch (Exception e) {
                    log.error(String.format("fail to convert field: {%s} caused by: %s", cfi.getField(), e.getLocalizedMessage()), e);
                }
            } else if (cfi.getField().getType().equals(LocalDate.class)) {
                try {
                    value = LocalDateTimeConverter.convert2Date(String.valueOf(value));
                } catch (Exception e) {
                    log.error(String.format("fail to convert field: {%s} caused by: %s", cfi.getField(), e.getLocalizedMessage()), e);
                }
            } else if (cfi.getField().getType().equals(BigDecimal.class)) {
                try {
                    value = BigDecimal.valueOf(Double.valueOf(String.valueOf(value)));
                } catch (NumberFormatException e) {
                    log.error(String.format("fail to convert field: {%s} caused by: %s", cfi.getField(), e.getLocalizedMessage()), e);
                }
            } else if (cfi.getField().getType().equals(Integer.class)) {
                try {
                    value = Integer.valueOf(String.valueOf(value));
                } catch (NumberFormatException e) {
                    log.error(String.format("fail to convert field: {%s} caused by: %s", cfi.getField(), e.getLocalizedMessage()), e);
                }
            } else if (cfi.getField().getType().equals(Long.class)) {
                try {
                    value = Long.valueOf(String.valueOf(value));
                } catch (NumberFormatException e) {
                    log.error(String.format("fail to convert field: {%s} caused by: %s", cfi.getField(), e.getLocalizedMessage()), e);
                }
            } else if (cfi.getField().getType().equals(Double.class)) {
                try {
                    value = Double.valueOf(String.valueOf(value));
                } catch (NumberFormatException e) {
                    log.error(String.format("fail to convert field: {%s} caused by: %s", cfi.getField(), e.getLocalizedMessage()), e);
                }
            } else if (cfi.getField().getType().equals(Date.class)) {
                try {
                    value = DateConverter.convert2Date(String.valueOf(value));
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(String.format("fail to convert field: {%s} caused by: %s", cfi.getField(), e.getLocalizedMessage()), e);
                }
            }

            try {
                FieldUtils.writeField(model, cfi.getField().getName(), value, true);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("write field value error: {}, value: {}", cfi, value);
            }

        }
    }

    private static Object getColumnValue(TiDbDrainerBinlog.ColumnInfo columnInfo, TiDbDrainerBinlog.Column column) {
        if (column.getIsNull())
            return null;

        if (columnInfo.getMysqlType().equals("int")
                || columnInfo.getMysqlType().equals("tinyint")
                || columnInfo.getMysqlType().equals("bigint"))
            return column.getInt64Value() + column.getUint64Value();

        if (columnInfo.getMysqlType().equals("double") || columnInfo.getMysqlType().equals("float")) {
            return column.getDoubleValue();
        }

        return column.getStringValue();
    }

    private static class DateConverter {
        static final String datePattern = "yyyy-MM-dd HH:mm:ss";

        static Date convert2Date (String dateStr) throws ParseException {
            SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
            return sdf.parse(dateStr);
        }
    }

    private static class LocalDateTimeConverter {
        static final String commonPattern = ""
                + "[yyyy/MM/dd HH:mm:ss.SSSSSS]"
                + "[yyyy-MM-dd HH:mm:ss[.SSS]]"
                + "[ddMMMyyyy:HH:mm:ss.SSS[ Z]]";

        static final String nanoPattern = "yyyy-MM-dd HH:mm:ss.SSSSSS";

        static final String nullDate = "0000-00-00 00:00:00";

        static final String date2TimePattern = "yyyy-MM-dd 00:00:00";

        static final String datePattern = "yyyy-MM-dd";

        public static LocalDateTime convert2Time(String dateStr) {
            if (Objects.isNull(dateStr))
                return null;

            if (Objects.equals(dateStr, nullDate))
                return null;

            if (dateStr.length() == nanoPattern.length())
                return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(nanoPattern));

            if (dateStr.length() == datePattern.length())
                return LocalDateTime.parse(dateStr + " 00:00:00", DateTimeFormatter.ofPattern(commonPattern));

            return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(commonPattern));
        }

        public static LocalDate convert2Date(String dateStr) {
            if (Objects.isNull(dateStr))
                return null;

            if (dateStr.length() != datePattern.length())
                return null;

            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(datePattern));
        }

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColumnFieldInfo {
        TiDbDrainerBinlog.ColumnInfo columnInfo;
        TiDbDrainerBinlog.Column column;
        Field field;
    }

}
