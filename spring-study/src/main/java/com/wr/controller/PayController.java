package com.wr.controller;

import com.wr.factory.PayServiceFactory;
import com.wr.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author : WangRui
 * @date : 2023/3/2
 */

@RestController // 等价于下面两个注解
//@Controller
//@ResponseBody
public class PayController {


//    @Autowired
//    PayServiceFactory payServiceFactory;

    @GetMapping("/pay")
    public void pay(@RequestParam(value = "payTypeCode",required = true) String payTypeCode) throws Exception {

        // 根据支付编码获取支付服务
        PayService payService = PayServiceFactory.getPayService(payTypeCode);
//        PayService payService1 = payServiceFactory.getPayService1(payTypeCode);

        //实际的支付操作
        payService.pay();
//        payService1.pay();

    }
}
