package cn.wr.udf;

import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * @author RWang
 * @Date 2022/2/25
 */

public class Unicode2StringDemoUdf extends UDF {

    private final StringBuilder sb = new StringBuilder();

    public String evaluate(String unicode){
        String[] hex = unicode.split("\\\\u");

        try {
            for (int i = 0; i < hex.length; i++) {
                int data = Integer.parseInt(hex[i].replaceAll("\\\\", ""), 16);
                sb.append((char) data);
            }
        } catch (Exception e){
            return sb.toString();
        }
        return sb.toString();
    }

}
