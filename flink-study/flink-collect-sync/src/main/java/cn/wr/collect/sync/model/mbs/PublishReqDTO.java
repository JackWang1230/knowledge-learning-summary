package cn.wr.collect.sync.model.mbs;

import java.io.Serializable;

public class PublishReqDTO implements Serializable {
    private static final long serialVersionUID = 6216669875226882879L;
    /**
     * 消息Topic
     */
    private String topic;
    /**
     * 消息Tag
     */
    private String tag;
    /**
     * 消息内容
     */
    private Object message;
    /**
     * 消息内容生成唯一主键
     */
    private String reqNo;
    /**
     * 业务主键
     */
    private String msgKey;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public String getReqNo() {
        return reqNo;
    }

    public void setReqNo(String reqNo) {
        this.reqNo = reqNo;
    }

    public String getMsgKey() {
        return msgKey;
    }

    public void setMsgKey(String msgKey) {
        this.msgKey = msgKey;
    }
}
