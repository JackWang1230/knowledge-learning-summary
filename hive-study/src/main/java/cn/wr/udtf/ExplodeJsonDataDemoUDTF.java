package cn.wr.udtf;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

import java.util.ArrayList;

/**
 *
 *
 * 参考 https://www.cnblogs.com/ggjucheng/archive/2013/02/01/2888819.html
 * @author RWang
 * @Date 2022/2/25
 */

public class ExplodeJsonDataDemoUDTF extends GenericUDTF {


    @Override
    public StructObjectInspector initialize(ObjectInspector[] args) throws UDFArgumentException {


        if (args.length != 1) {
            throw new UDFArgumentLengthException("ExplodeMap takes only one argument");
        }
        if (args[0].getCategory() != ObjectInspector.Category.PRIMITIVE) {
            throw new UDFArgumentException("ExplodeMap takes string as a parameter");
        }

        // 此处代表输出的列名和类型 输出一共两列，第一列是month，第二列是amount 并且都是string类型
        ArrayList<String> fieldNames = new ArrayList<String>();
        ArrayList<ObjectInspector> fieldOIs = new ArrayList<ObjectInspector>();

        // 这是第一列 输出的列名是month
        fieldNames.add("month"); // month
        // 这是第一列 输出的类型是string
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);

        // 这是第二列 输出的列名是amount
        fieldNames.add("amount"); // amount
        // 这是第二列 输出的类型是string
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);

        // 返回值的类型代表了输出的列名和类型
        return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames,fieldOIs);
    }

/*    @Override
    public StructObjectInspector initialize(StructObjectInspector argOIs) throws UDFArgumentException {

        return super.initialize(argOIs);
    }*/

    @Override
    public void process(Object[] args) throws HiveException {

        String s = args[0].toString();
        JSONObject jsonObject = JSONObject.parseObject(s);
        JSONArray salary = jsonObject.getJSONArray("salary");
        for (Object o : salary) {
            JSONObject jsonObject1 = JSONObject.parseObject(o.toString());
            String month = jsonObject1.getString("month");
            String amount = jsonObject1.getString("amount");
            // 将多个字符串存入数组
            String[] strings = {month, amount};
            forward(strings);
        }
    }

    @Override
    public void close() throws HiveException {

    }

    public static void main(String[] args) {

        String json="{\"id\":\"11\",\"name\":\"jack\",\"salary\":[{\"month\":\"1\",\"amount\":1234},{\"month\":\"2\",\"amount\":2345}]}";
        JSONObject jsonObject = JSONObject.parseObject(json);
        JSONArray salary = jsonObject.getJSONArray("salary");
        for (Object o : salary) {

            JSONObject jsonObject1 = JSONObject.parseObject(o.toString());
            String month = jsonObject1.getString("month");
            String amount = jsonObject1.getString("amount");
            // 将多个字符串存入数组
            String[] strings = {month, amount};
            // forward(strings);
        }
    }
}
