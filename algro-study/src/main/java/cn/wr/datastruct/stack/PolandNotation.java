package cn.wr.datastruct.stack;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * ascii表中 常量0-127
 *    1.数字0-9在asc中对应的是 48-57
 *    2.大写字母A-Z对应的是 65-90
 *    3.小写字母a-z对应的是 97-122
 *
 * @author : WangRui
 * @date : 2022/11/22
 */

public class PolandNotation {


    /**
     *  解析中缀表达式放入到一个数组中
     *  String a = 12+((2+3)*4)-5
     * @param s
     * @return
     */
    public List<String> toInfixExpressionList(String s){

        int index=0; //记录下标
        char c; // 记录下标位置的值
        ArrayList<String> list = new ArrayList<>(); // 存放数据
        do {
            // 非数字逻辑
           if ((c=s.charAt(index))<48 || (c=s.charAt(index))>57){
               list.add(""+c);
               index++;
           }else {
               String str = ""; // 用于拼接多位数
               // 存放多位数 asci对应0-9（48-57）
               while (index<s.length() && (c=s.charAt(index))>=48 && (c=s.charAt(index))<=57){
                   str += c;
                   index++;
               }
               list.add(str);
           }

        }while (index<s.length());

        return list;

    }

    public static void main(String[] args) {
        try {
            String a = "12+((2+3)*4)-5";
            PolandNotation polandNotation = PolandNotation.class.newInstance();
            List<String> strings = polandNotation.toInfixExpressionList(a);
            System.out.println("end");

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
