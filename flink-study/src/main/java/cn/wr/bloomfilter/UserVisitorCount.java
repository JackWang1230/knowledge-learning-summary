package cn.wr.bloomfilter;

/**
 * @author : WangRui
 * @date : 2022/10/10
 */

public class UserVisitorCount {

    private String uv;
    private String windowEnd;
    private int i;
    public UserVisitorCount(String uv, String windowEnd, int i) {
        this.uv=uv;
        this.windowEnd=windowEnd;
        this.i = i;
    }
}
