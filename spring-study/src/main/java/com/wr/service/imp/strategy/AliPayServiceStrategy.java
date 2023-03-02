package com.wr.service.imp.strategy;

import com.wr.annotation.PayType;
import com.wr.enums.PayTypeEnum;
import com.wr.service.PayService;
import org.springframework.stereotype.Service;

/**
 * @author : WangRui
 * @date : 2023/3/2
 */

@Service
@PayType(value = PayTypeEnum.ALI)
public class AliPayServiceStrategy implements PayService {
    @Override
    public void pay() {
        System.out.println("支付宝支付成功");
    }
}
