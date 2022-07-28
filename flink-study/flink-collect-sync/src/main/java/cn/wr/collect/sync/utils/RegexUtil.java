package cn.wr.collect.sync.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
    private static final String IS_NUMERIC = "^[0-9]*$";

    /**
     * 利用正则表达式判断字符串是否是数字
     * @param str
     * @return
     */
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile(IS_NUMERIC);
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    public static void main(String[] args) {
        boolean result = RegexUtil.isNumeric("a");
        System.out.println(result);
    }
}
