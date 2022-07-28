package cn.wr.collect.sync.constants;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 标准商品字段枚举
 */
public enum StandardEnum {
    gc_goods_overweight,
    gc_base_nootc,
    gc_goods_dosage,
    gc_goods_spu_attr_syncrds,
    gc_goods_attr_info_syncrds,
    gc_goods_spu,
    ;

    StandardEnum() {
    }

    public static StandardType getType(String name, String fieldName) {
        StandardEnum standardEnum = Arrays.stream(StandardEnum.values())
                .filter(val -> StringUtils.equals(val.name(), name))
                .findFirst().orElse(null);
        if (Objects.isNull(standardEnum)) {
            return null;
        }
        switch (standardEnum) {
            case gc_goods_overweight:
                return StandardGoodsOverweight.getType(fieldName);
            case gc_base_nootc:
                return StandardBaseNootc.getType(fieldName);
            case gc_goods_dosage:
                return StandardGoodsDosage.getType(fieldName);
            case gc_goods_spu_attr_syncrds:
                return StandardSpuAttr.getType(fieldName);
            case gc_goods_spu:
                return StandardGoodsSpu.getType(fieldName);
            default:
                return null;
        }
    }

    public static String[] getMap(String name, String fieldName) {
        StandardEnum standardEnum = Arrays.stream(StandardEnum.values())
                .filter(val -> StringUtils.equals(val.name(), name))
                .findFirst().orElse(null);
        if (Objects.isNull(standardEnum)) {
            return null;
        }
        switch (standardEnum) {
            case gc_goods_overweight:
                return StandardGoodsOverweight.getMap(fieldName);
            case gc_base_nootc:
                return StandardBaseNootc.getMap(fieldName);
            case gc_goods_dosage:
                return StandardGoodsDosage.getMap(fieldName);
            case gc_goods_spu_attr_syncrds:
                return StandardSpuAttr.getMap(fieldName);
            case gc_goods_spu:
                return StandardGoodsSpu.getMap(fieldName);
            default:
                return null;
        }
    }

    public static List<String> getMap(String name) {
        StandardEnum standardEnum = Arrays.stream(StandardEnum.values())
                .filter(val -> StringUtils.equals(val.name(), name))
                .findFirst().orElse(null);
        if (Objects.isNull(standardEnum)) {
            return Collections.emptyList();
        }
        switch (standardEnum) {
            case gc_goods_overweight:
                return StandardGoodsOverweight.getMap();
            case gc_base_nootc:
                return StandardBaseNootc.getMap();
            case gc_goods_dosage:
                return StandardGoodsDosage.getMap();
            case gc_goods_spu_attr_syncrds:
                return StandardSpuAttr.getMap();
            case gc_goods_spu:
                return StandardGoodsSpu.getMap();
            default:
                return null;
        }
    }

    enum StandardGoodsOverweight {
        trade_code(StandardType.key, null),
        is_overweight(StandardType.field, new String[]{"is_overweight"});

        private final StandardType type;
        private final String[] map;

        StandardGoodsOverweight(StandardType type, String[] map) {
            this.type = type;
            this.map = map;
        }

        public static StandardType getType(String name) {
            StandardGoodsOverweight standard = Arrays.stream(StandardGoodsOverweight.values())
                    .filter(val -> StringUtils.equals(val.name(), name))
                    .findFirst().orElse(null);
            return Objects.isNull(standard) ? null : standard.type;
        }

        public static String[] getMap(String name) {
            StandardGoodsOverweight standard = Arrays.stream(StandardGoodsOverweight.values())
                    .filter(val -> StringUtils.equals(val.name(), name))
                    .findFirst().orElse(null);
            return Objects.isNull(standard) ? null : standard.map;
        }

        public static List<String> getMap() {
            List<String> collect = Arrays.stream(StandardGoodsOverweight.values())
                    .filter(val -> Objects.nonNull(val.map))
                    .flatMap(val -> Arrays.stream(val.map))
                    .collect(Collectors.toList());
            return CollectionUtils.isEmpty(collect) ? Collections.emptyList() : collect;
        }
    }

    enum StandardBaseNootc {
        approval_number(StandardType.key, null),
        otc_type(StandardType.field, new String[]{"is_prescription"});

        private final StandardType type;
        private final String[] map;

        StandardBaseNootc(StandardType type, String[] map) {
            this.type = type;
            this.map = map;
        }

        public static StandardType getType(String name) {
            StandardBaseNootc standard = Arrays.stream(StandardBaseNootc.values())
                    .filter(val -> StringUtils.equals(val.name(), name))
                    .findFirst().orElse(null);
            return Objects.isNull(standard) ? null : standard.type;
        }

        public static String[] getMap(String name) {
            StandardBaseNootc standard = Arrays.stream(StandardBaseNootc.values())
                    .filter(val -> StringUtils.equals(val.name(), name))
                    .findFirst().orElse(null);
            return Objects.isNull(standard) ? null : standard.map;
        }

        public static List<String> getMap() {
            List<String> collect = Arrays.stream(StandardBaseNootc.values())
                    .filter(val -> Objects.nonNull(val.map))
                    .flatMap(val -> Arrays.stream(val.map))
                    .collect(Collectors.toList());
            return CollectionUtils.isEmpty(collect) ? Collections.emptyList() : collect;
        }
    }

    enum StandardGoodsDosage {
        trade_code(StandardType.key, null),
        prescription_type(StandardType.field, new String[]{"is_prescription"});

        private final StandardType type;
        private final String[] map;

        StandardGoodsDosage(StandardType type, String[] map) {
            this.type = type;
            this.map = map;
        }

        public static StandardType getType(String name) {
            StandardGoodsDosage standard = Arrays.stream(StandardGoodsDosage.values())
                    .filter(val -> StringUtils.equals(val.name(), name))
                    .findFirst().orElse(null);
            return Objects.isNull(standard) ? null : standard.type;
        }

        public static String[] getMap(String name) {
            StandardGoodsDosage standard = Arrays.stream(StandardGoodsDosage.values())
                    .filter(val -> StringUtils.equals(val.name(), name))
                    .findFirst().orElse(null);
            return Objects.isNull(standard) ? null : standard.map;
        }

        public static List<String> getMap() {
            List<String> collect = Arrays.stream(StandardGoodsDosage.values())
                    .filter(val -> Objects.nonNull(val.map))
                    .flatMap(val -> Arrays.stream(val.map))
                    .collect(Collectors.toList());
            return CollectionUtils.isEmpty(collect) ? Collections.emptyList() : collect;
        }
    }

    enum StandardSpuAttr {
        bar_code(StandardType.key, null),
        attr_id(StandardType.field, new String[]{"is_ephedrine"});

        private final StandardType type;
        private final String[] map;

        StandardSpuAttr(StandardType type, String[] map) {
            this.type = type;
            this.map = map;
        }

        public static StandardType getType(String name) {
            StandardSpuAttr standard = Arrays.stream(StandardSpuAttr.values())
                    .filter(val -> StringUtils.equals(val.name(), name))
                    .findFirst().orElse(null);
            return Objects.isNull(standard) ? null : standard.type;
        }

        public static String[] getMap(String name) {
            StandardSpuAttr standard = Arrays.stream(StandardSpuAttr.values())
                    .filter(val -> StringUtils.equals(val.name(), name))
                    .findFirst().orElse(null);
            return Objects.isNull(standard) ? null : standard.map;
        }

        public static List<String> getMap() {
            List<String> collect = Arrays.stream(StandardSpuAttr.values())
                    .filter(val -> Objects.nonNull(val.map))
                    .flatMap(val -> Arrays.stream(val.map))
                    .collect(Collectors.toList());
            return CollectionUtils.isEmpty(collect) ? Collections.emptyList() : collect;
        }
    }

    enum StandardAttrInfo {
        id(StandardType.key, null),
        attr_id(StandardType.field, new String[]{"is_ephedrine"});

        private final StandardType type;
        private final String[] map;

        StandardAttrInfo(StandardType type, String[] map) {
            this.type = type;
            this.map = map;
        }

        public static StandardType getType(String name) {
            StandardAttrInfo standard = Arrays.stream(StandardAttrInfo.values())
                    .filter(val -> StringUtils.equals(val.name(), name))
                    .findFirst().orElse(null);
            return Objects.isNull(standard) ? null : standard.type;
        }

        public static String[] getMap(String name) {
            StandardAttrInfo standard = Arrays.stream(StandardAttrInfo.values())
                    .filter(val -> StringUtils.equals(val.name(), name))
                    .findFirst().orElse(null);
            return Objects.isNull(standard) ? null : standard.map;
        }

        public static List<String> getMap() {
            List<String> collect = Arrays.stream(StandardAttrInfo.values())
                    .filter(val -> Objects.nonNull(val.map))
                    .flatMap(val -> Arrays.stream(val.map))
                    .collect(Collectors.toList());
            return CollectionUtils.isEmpty(collect) ? Collections.emptyList() : collect;
        }
    }

    enum StandardGoodsSpu {
        id(StandardType.key, null),
        approval_number(StandardType.field, new String[]{"approval_number"});

        private final StandardType type;
        private final String[] map;

        StandardGoodsSpu(StandardType type, String[] map) {
            this.type = type;
            this.map = map;
        }

        public static StandardType getType(String name) {
            StandardGoodsSpu standard = Arrays.stream(StandardGoodsSpu.values())
                    .filter(val -> StringUtils.equals(val.name(), name))
                    .findFirst().orElse(null);
            return Objects.isNull(standard) ? null : standard.type;
        }

        public static String[] getMap(String name) {
            StandardGoodsSpu standard = Arrays.stream(StandardGoodsSpu.values())
                    .filter(val -> StringUtils.equals(val.name(), name))
                    .findFirst().orElse(null);
            return Objects.isNull(standard) ? null : standard.map;
        }

        public static List<String> getMap() {
            List<String> collect = Arrays.stream(StandardGoodsSpu.values())
                    .filter(val -> Objects.nonNull(val.map))
                    .flatMap(val -> Arrays.stream(val.map))
                    .collect(Collectors.toList());
            return CollectionUtils.isEmpty(collect) ? Collections.emptyList() : collect;
        }
    }

    public enum StandardType {
        key,
        field
    }
}
