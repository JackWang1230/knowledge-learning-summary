package cn.wr.designPattern.sample;

public enum ErrorCode {

    OK(0),
    FAIL(1);

    Integer code;

    ErrorCode(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }


}
