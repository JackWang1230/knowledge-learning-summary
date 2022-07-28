package cn.wr.collect.sync.model.mbs;

import java.io.Serializable;

public class ResultDTO<T> implements Serializable {
    private static final long serialVersionUID = 974031606242853509L;
    private int errno = 0;
    private String error = "请求成功";
    private String dataType = "OBJECT";
    private T data;

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
