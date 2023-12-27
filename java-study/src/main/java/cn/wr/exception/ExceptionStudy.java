package cn.wr.exception;

/**
 * @author : WangRui
 * @date : 2023/10/31
 */

public class ExceptionStudy {

    public Integer getNums() throws Exception {
        return 1 / 0;
    }

    public String getStr() {
        String host = null;
        Integer nums = 0;
        try {
            nums = getNums();
        } catch (Exception e) {
            host = "ss";
            System.exit(0);
        }
        return host + nums;
    }


    public static void main(String[] args) {
        ExceptionStudy exceptionStudy = new ExceptionStudy();
        String str = exceptionStudy.getStr();
        System.out.println(str);
    }
}
