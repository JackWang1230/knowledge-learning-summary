package com.wr.enums;

public enum PayTypeEnum {

    WX("WX","微信支付"),
    ALI("ALI","支付宝");

    /**
     * 支付方式内部编码
     */
    private String code;

    /**
     * 支付方式名称
     */
    private String info;

    PayTypeEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

    /**
     * 根据code获取枚举
     * values() 枚举类中自带遍历的方法
     * @param code
     * @return
     */
    public static PayTypeEnum getValues(String code){
        for (PayTypeEnum payTypeEnum : values()) {
            if (payTypeEnum.code.equals(code)){
                return payTypeEnum;
            }
        }
        return null;
    }
}
