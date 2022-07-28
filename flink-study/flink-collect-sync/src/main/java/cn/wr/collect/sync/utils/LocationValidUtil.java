package cn.wr.collect.sync.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class LocationValidUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationValidUtil.class);


    public  static  String longRegex = "^(\\-|\\+)?(([1-9]\\d?)|(1[0-7]\\d))(\\.\\d{1,10})|180|0(\\.\\d{1,10})?$";

    public  static String laRegex = "^(\\-|\\+)?(([1-8]\\d?)|([1-8]\\d))(\\.\\d{1,10})|90|0(\\.\\d{1,10})?$";

    /**
     * 经度校验
     *
     * @param value
     * @return
     */
    public static boolean isLONG(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        return Pattern.matches(longRegex, value);
    }

    /**
     * 纬度校验
     *
     * @param value
     * @return
     */
    public static boolean isLA(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        return Pattern.matches(laRegex, value);
    }

    public static void main(String[] args) {
        String lo = "37.571111";
        String lati = "121.352222";
        LOGGER.info("------------->lati:"+isLA(lati));
        LOGGER.info("------------->lo:"+isLONG(lo));

    }

}
