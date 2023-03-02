package cn.wr.designPattern.sample;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author : WangRui
 * @date : 2023/2/28
 */


@ApiModel(value = "响应结果")
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = -6190689122701100762L;

    /**
     * 响应编码
     */
    @ApiModelProperty(value = "响应编码:0-请求处理成功")
    private Integer code = 0;
    /**
     * 提示消息
     */
    @ApiModelProperty(value = "提示消息")
    private String message;

    /**
     * 请求路径
     */
    @ApiModelProperty(value = "请求路径")
    private String path;

    /**
     * 响应数据
     */
    @ApiModelProperty(value = "响应数据")
    private T data;

    /**
     * http状态码
     */
    private Integer httpStatus;

    /**
     * 附加数据
     */
    @ApiModelProperty(value = "附加数据")
    private Map<String, Object> extra;

    /**
     * 响应时间
     */
    @ApiModelProperty(value = "响应时间")
    private Long timestamp = System.currentTimeMillis();

    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    public Integer getHttpStatus() {
        return httpStatus;
    }

    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    public boolean isOk() {
        return this.code == ErrorCode.OK.getCode();
    }

    public static Result ok() {
        return new Result().code(ErrorCode.OK.getCode());
    }

    public static Result failed() {
        return new Result().code(ErrorCode.FAIL.getCode());
    }

    public Result code(Integer code) {
        this.code = code;
        return this;
    }

    public Result msg(String message) {
        if (message == null) {
            message = "";
        }
//        this.message = i18n(message,message);
        this.message = message;
        return this;
    }

    public Result data(T data) {
        this.data = data;
        return this;
    }

    public Result path(String path) {
        this.path = path;
        return this;
    }

    public Result httpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public Result put(String key, Object value) {
        if (this.extra == null) {
            this.extra = Maps.newHashMap();
        }
        this.extra.put(key, value);
        return this;
    }


}

