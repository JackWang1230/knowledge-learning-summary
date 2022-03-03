package cn.wr.calcite;


import com.alibaba.fastjson.JSONObject;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @author RWang
 * @Date 2022/3/3
 */

public class CalciteSql {

    public static void main(String[] args) {
        FrameworkConfig config = Frameworks.newConfigBuilder()
                .parserConfig(SqlParser.Config.DEFAULT)
                .build();

        String sql = "select id,name,age from test";
        SqlParser sqlParser = SqlParser.create(sql, config.getParserConfig());
        try {
            SqlNode sqlNode = sqlParser.parseStmt();
            System.out.println(sqlNode.toString());
        } catch (SqlParseException e) {
            e.printStackTrace();
        }
        BigDecimal bigDecimal = new BigDecimal(StringUtils.isBlank(null)?0:1);
        String json ="{\"id\":\"11\",\"name\":\"jack\",\"salary\":[{\"month\":\"1\",\"amount\":1234},{\"month\":\"2\",\"amount\":2345}]}";

        JSONObject jsonObject = JSONObject.parseObject(json);


        String dd = jsonObject.getString("dd");
        System.out.println(dd);
        BigDecimal dd1= new BigDecimal(StringUtils.isBlank(dd) ? "0.00" :dd);
        System.out.println(dd1);


        Long ddddd = jsonObject.getLong("ddddd");
        System.out.println(ddddd);


        int dd2 = jsonObject.getIntValue("Dd");
        System.out.println(dd2);

        long ee = jsonObject.getLongValue("ee");
        System.out.println(ee);
        BigDecimal ddsds = jsonObject.getBigDecimal("ddsds");
        if (ddsds == null) {
            System.out.println("yes");
        }else {
            System.out.println("no");
        }

        System.out.println(ddsds);

        System.out.println(bigDecimal.toString());
    }
}
