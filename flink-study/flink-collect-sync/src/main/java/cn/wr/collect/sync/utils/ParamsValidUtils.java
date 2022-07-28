package cn.wr.collect.sync.utils;

import java.util.regex.Pattern;

/**
 * 参数合法校验
 */
public class ParamsValidUtils {

    /**
     * 经纬度校验
     * @param longitude
     * @param latitude
     * @return
     */
    public static boolean gpsValid(String longitude, String latitude) {
        // 经度： -180.0～+180.0（整数部分为0～180，必须输入1到8位小数）
        String longitudePattern = "^[\\-\\+]?(0?\\d{1,2}\\.\\d{1,8}|1[0-7]?\\d{1}\\.\\d{1,8}|180\\.0{1,8})$";
        // 纬度： -90.0～+90.0（整数部分为0～90，必须输入1到8位小数）
        String latitudePattern = "^[\\-\\+]?([0-8]?\\d{1}\\.\\d{1,8}|90\\.0{1,8})$";
        return Pattern.matches(longitudePattern, longitude) && Pattern.matches(latitudePattern, latitude);
    }

    public static void main(String[] args) {
        boolean b = ParamsValidUtils.gpsValid("30.0", "30.0");
        System.out.println(b);
    }
}
