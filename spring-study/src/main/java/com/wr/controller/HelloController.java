package com.wr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author : WangRui
 * @date : 2023/3/2
 */

@Controller
public class HelloController {

    /**
     * 第一个springboot程序
     * @return
     */
    @RequestMapping("/index")
    public String sayHello(){
        return "index";
    }
}
