package cn.wr.enums;

/**
 * 相比静态常量 占用空间大一些，可读性好一些
 */
public enum MysqlVersion {

    V5("5.5"),
    V6("5.6"),
    V7("5.7");

    private String version;

    MysqlVersion(String version) {
        this.version =version;
    }

    public String getVersion(){
        return version;
    }


    public static void main(String[] args) {
        MysqlVersion v5 = V5;
        String version = V5.version;
        System.out.println(version);
        String version1 = V5.getVersion();
        System.out.println(version1);
    }

}
