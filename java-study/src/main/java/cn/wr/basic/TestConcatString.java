package cn.wr.basic;

/**
 * @author RWang
 * @Date 2022/5/30
 */

public class TestConcatString {


    public static void main(String[] args) {
        String str=new String("000");
        long start=System.currentTimeMillis();//获取拼接前ms的时间戳
        for(int i=0;i<10000;i++) {
            str+="第"+i;
        }
        long end=System.currentTimeMillis();//获取拼接后ms的时间戳
        long s=end-start;
        System.out.println("string需要"+s+"ms");
        StringBuilder str1=new StringBuilder("000");
        long start1=System.currentTimeMillis();
        for(int i=0;i<10000;i++) {
            str1.append("第"+i);
        }
        long end1=System.currentTimeMillis();
        long s1=end1-start1;
        System.out.println("stringbuilder需要"+s1+"ms");
        StringBuffer str2=new StringBuffer("000");
        long start2=System.currentTimeMillis();
        for(int i=0;i<10000;i++) {
            str2.append("第"+i);
        }
        long end2=System.currentTimeMillis();
        long s2=end2-start2;
        System.out.println("StringBuffer需要"+s2+"ms");
    }
}
