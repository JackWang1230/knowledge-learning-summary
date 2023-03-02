package com.wr.factory;

import com.wr.annotation.PayType;
import com.wr.enums.PayTypeEnum;
import com.wr.service.PayService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : WangRui
 * @date : 2023/3/2
 */

@Component
public class PayServiceFactory implements ApplicationContextAware {


    /**
     * 支付方式枚举-支付服务的映射集合
     */
    private static final Map<PayTypeEnum, PayService> payServiceMapping = new HashMap<>();


    /**
     * 工厂方法获取支付服务实现
     * @param payTypeCode 支付方式的内部编码
     * @return
     * @throws Exception
     */
    public static final PayService getPayService(String payTypeCode) throws Exception {
        PayTypeEnum payTypeEnum = PayTypeEnum.getValues(payTypeCode);
        if (payTypeEnum == null){
            throw new Exception("无效的支付编码");
        }
        PayService payService = payServiceMapping.get(payTypeEnum);

        if (payService == null){
            throw new Exception("没有匹配的支付服务实现类");
        }
        return payService;
    }

    /**
     * 工厂方法获取支付服务实现
     * @param payTypeCode 支付方式的内部编码
     * @return
     * @throws Exception
     */
    public  PayService getPayService1(String payTypeCode) throws Exception {
        PayTypeEnum payTypeEnum = PayTypeEnum.getValues(payTypeCode);
        if (payTypeEnum == null){
            throw new Exception("无效的支付编码");
        }
        PayService payService = payServiceMapping.get(payTypeEnum);

        if (payService == null){
            throw new Exception("没有匹配的支付服务实现类");
        }
        return payService;
    }



    /**
     * 初始化（支付枚举-支付服务）的映射
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        Map<String, Object> payServiceMap = applicationContext.getBeansWithAnnotation(PayType.class);

        if (CollectionUtils.isEmpty(payServiceMap)){
            throw new RuntimeException("支付服务映射初始化失败");
        }
        payServiceMap.forEach((k,bean)->{
            if (!(bean instanceof PayService)){
                throw new RuntimeException("注解："+PayType.class+",只能用于"+PayService.class+"的实现类");
            }
            PayService payService =(PayService)bean;
            PayType annotation = payService.getClass().getAnnotation(PayType.class);
            payServiceMapping.put(annotation.value(),payService);
        });

    }
}
