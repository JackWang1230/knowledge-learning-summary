package cn.wr.collect.sync.utils;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharacterUtil {
    private static final Logger log = LoggerFactory.getLogger(CharacterUtil.class);
    private static final Pattern pattern = Pattern.compile("\\s*|\t*|\r*|\n*");

    private static final Character[] SPECIAL_SYMBOL = {'$', '+', 'Ⅰ', 'Ⅱ', '|', 'μ', '='};

    /**
     * 判断字符是否是中文
     *
     * @param c 字符
     * @return 是否是中文
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.VERTICAL_FORMS;
    }



    public static boolean isMessyCode(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        try {
            Matcher m = pattern.matcher(value);
            String after = m.replaceAll("");
            String temp = after.replaceAll("\\p{P}", "");
            char[] ch = temp.trim().toCharArray();
            float chLength = 0;
            float count = 0;
            for (char c : ch) {
                if (Arrays.asList(SPECIAL_SYMBOL).contains(c)) {
                    continue;
                }
                if (!(CharUtils.isAsciiAlpha(c) || CharUtils.isAsciiNumeric(c))) {
                    if (!isChinese(c)) {
                        count = count + 1;
                    }
                    chLength++;
                }
            }
            float result = count / chLength ;
            return result > 0.4;
        }
        catch (Exception e) {
            log.error("CharacterUtil str:{} Exception:{}", value, e);
        }
        return false;
    }

    /*public static boolean isMessyCode(String strName) {
        try {
            Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
            Matcher m = p.matcher(strName);
            String after = m.replaceAll("");
            String temp = after.replaceAll("\\p{P}", "");
            char[] ch = temp.trim().toCharArray();

            int length = (ch != null) ? ch.length : 0;
            for (int i = 0; i < length; i++) {
                char c = ch[i];
                if (!(CharUtils.isAsciiAlpha(c) || CharUtils.isAsciiNumeric(c))) {
                    String str = "" + ch[i];
                    if (!str.matches("[\u4e00-\u9fa5]+")) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            log.error("CharacterUtil Exception:{}", e);
        }

        return false;
    }*/

    public static void main(String[] args) {
        String json01 = "YZ|京0451-2007";
//        String json01 = "Ã©Å¸Â©Ã©Â¡ÂºÃ¥Â¹Â³";
        // String json01 = "{\"approvalNumber\":\"Ã©Å¸Â©Ã©Â¡ÂºÃ¥Â¹Â³\",\"commonName\":\"三合一豪华按摩机　多功能\",\"dbId\":74,\"form\":\"XM-S602\",\"gmtcreated\":\"2019-10-18T10:54:43\",\"gmtupdated\":\"2021-08-23T14:20:28\",\"id\":1739,\"internalId\":\"62245\",\"manufacturer\":\"福安市鑫美电子有限公司\",\"merchantId\":674,\"merchantName\":\"青岛春天之星大药房医药连锁有限公司\",\"operate\":\"update\",\"pack\":\"台\",\"price\":1280.0,\"status\":0,\"tableId\":22364,\"tradeCode\":\"\"}";
//        String json02 = "{\"approvalNumber\":null,\"commonName\":\"琅琊台尚品 52°（1840） \",\"dbId\":74,\"form\":\"480ml\",\"gmtcreated\":\"2019-10-18T10:54:43\",\"gmtupdated\":\"2021-08-23T14:20:28\",\"id\":1436,\"internalId\":\"80002\",\"manufacturer\":\"青岛琅琊台集团股份有限公司\",\"merchantId\":674,\"merchantName\":\"青岛春天之星大药房医药连锁有限公司\",\"operate\":\"update\",\"pack\":\"盒\",\"price\":60.0,\"status\":0,\"tableId\":22061,\"tradeCode\":\"6903568111840\"}";
        System.out.println(isMessyCode(json01));
        System.out.println(json01);

    }
}
