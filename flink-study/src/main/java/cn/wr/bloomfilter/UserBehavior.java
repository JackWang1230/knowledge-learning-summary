package cn.wr.bloomfilter;

/**
 * @author : WangRui
 * @date : 2022/10/10
 */

public class UserBehavior {

    private String userId;
    private long timeStamp;
    private long parseLong;
    private long parseLong1;
    private int parseInt;

    public UserBehavior(long parseLong, long parseLong1, int parseInt, String s, long timeStamp) {
        this.parseLong=parseLong;
        this.parseLong1=parseLong1;
        this.parseInt=parseInt;
        this.userId = s;
        this.timeStamp=timeStamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getParseLong() {
        return parseLong;
    }

    public void setParseLong(long parseLong) {
        this.parseLong = parseLong;
    }

    public long getParseLong1() {
        return parseLong1;
    }

    public void setParseLong1(long parseLong1) {
        this.parseLong1 = parseLong1;
    }

    public int getParseInt() {
        return parseInt;
    }

    public void setParseInt(int parseInt) {
        this.parseInt = parseInt;
    }
}
