package cn.wr.collect.sync.utils;

import com.google.common.hash.Hashing;

import java.nio.charset.Charset;
import java.util.Objects;

public class GenerateGoodsNoUtil {
    public static String generate(final Integer goodsType,
                                         final Integer goodsSubType,
                                         final String internalId,
                                         final String goodsName,
                                         final String approvalNumber,
                                         final String customSuffix) {
        final Integer oldParentType = goodsSubType / 10;
        StringBuilder sb = new StringBuilder()
                .append(oldParentType)
                .append(goodsSubType)
                .append(internalId);
        if (Objects.nonNull(approvalNumber))
            sb.append(approvalNumber);

        if (Objects.nonNull(goodsName)) {
            sb.append(goodsName);
        }
        String suffixPrev = generateGoodsNoSuffix(sb.toString());
        String type = generateTypeIdentifier(oldParentType);
        String subType = generateTypeIdentifier(goodsSubType);
        final String prefix = type + subType;
        final String middle = "00";
        String secondHashSeed = prefix + suffixPrev;
        if (Objects.nonNull(customSuffix)) {
            secondHashSeed = secondHashSeed + customSuffix;
        }
        final String suffix = generateGoodsNoSuffix(secondHashSeed);
        return prefix + middle + suffix;
    }

    private static String generateGoodsNoSuffix(String goodsInfos) {
        return Hashing.murmur3_32().hashString(goodsInfos, Charset.forName("UTF-8")).toString().toUpperCase();
    }

    private static String generateTypeIdentifier(Integer type) {
        if (Objects.isNull(type))
            return "00";
        if (type < 10)
            return String.valueOf(type * 10);
        return String.valueOf(type);
    }
}
