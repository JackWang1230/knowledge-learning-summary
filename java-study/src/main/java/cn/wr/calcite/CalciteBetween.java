package cn.wr.calcite;

import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;

/**
 * @author RWang
 * @Date 2022/3/25
 */

public class CalciteBetween {

    public static void main(String[] args) {

        FrameworkConfig config = Frameworks.newConfigBuilder()
                .parserConfig(SqlParser.Config.DEFAULT)
                .build();

        String sql = "select id,name,age,datetime from test where datetime between 2021-10-10 00:00:00 and 2021-10-11 00:00:00 ";
        SqlParser sqlParser = SqlParser.create(sql, config.getParserConfig());
        try {
            SqlNode sqlNode = sqlParser.parseStmt();
            System.out.println(sqlNode.toString());
        } catch (SqlParseException e) {
            e.printStackTrace();
        }
    }
}
